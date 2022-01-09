package com.rchat.net.services;

import com.eventsystem.components.EventBus;
import com.eventsystem.events.Event;
import com.rchat.events.net.ResponseEvent;
import com.rchat.events.services.NetServiceStartEvent;
import com.rchat.models.DataAccess;
import com.rchat.net.exceptions.MessageOversizeException;
import com.rchat.net.messages.Message;
import com.rchat.net.messages.responses.Response;
import com.rchat.net.messages.responses.ResponseFactory;
import com.rchat.net.utils.ProtocolConstants;
import com.rchat.net.utils.ServiceDebugLogger;
import com.rchat.utils.XMLUtil;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;

/**
 * Is responsible for reading, parsing and managing
 * bytes-formed incoming messages from a socket's input stream.
 *
 * @author Ron
 * @since 1.0
 */
public class MessageReader extends AbstractRunnableService {

    /**
     * The capacity of the MessageReader's data buffer (in bytes).
     * Or the maximum number of bytes can be handled in a single
     * extraction.
     *
     * @see #buffer
     * @see #bytesRead
     */
    public final int CAPACITY = 65536;

    public static final String RECEIVER_TOPIC = "Receiver";
    public static final String RESPONSES_TOPIC = "ResponsesListener";


    // Buffers incoming messages bytes (In-Order) from the input stream.
    private final byte[] buffer;
    // Number of bytes read to the buffer which make up a single message
    private int bytesRead;

    // The message currently handled
    private Message message;

    // The input stream connected to the program's socket
    private DataInputStream inputStream;

    public MessageReader(EventBus eventBus, DataAccess dataAccess){
        super(eventBus, dataAccess);

        eventBus.createTopic(RECEIVER_TOPIC);
        eventBus.createTopic(RESPONSES_TOPIC);
        eventBus.subscribe(this, RECEIVER_TOPIC);

        buffer = new byte[CAPACITY];
        bytesRead = 0;

        message = null;
    }

    protected void setup(@NotNull NetServiceStartEvent event) throws IOException {
        inputStream = new DataInputStream(event.getSocket().getInputStream());
    }

    protected void teardown() {
        ServiceDebugLogger.log(this.getClass(), "Cleaning resources and resetting the buffer's marker...");

        clean(inputStream);
        resetMarker();
    }

    private void resetMarker() {
        bytesRead = 0;
    }

    /**
     * Reads message bytes.
     *
     * @throws IOException                  if stream problems occurred.
     * @throws NumberFormatException        if the parsed length of this message is not a number.
     * @throws MessageOversizeException     if the message is bigger than the buffer's capacity.
     */
    private void read() throws IOException, NumberFormatException, MessageOversizeException {
        // Reading/buffering the message's header, which is fixed in size
        for (int i = 0; i < ProtocolConstants.LENGTH_ATTR_INDEX + ProtocolConstants.HEADER_ATTRS_LENGTH; i++)
            buffer[bytesRead++] = inputStream.readByte();
        int length = Integer.parseInt(new String(Arrays.copyOfRange(buffer, ProtocolConstants.LENGTH_ATTR_INDEX, bytesRead)));

        if (length > CAPACITY)
            throw new MessageOversizeException(length, CAPACITY);
        while (bytesRead < length)
            buffer[bytesRead++] = inputStream.readByte();
        dataAccess.getSessionData().addBytesReceived(bytesRead);
    }

    private Response parseMessageBytes() throws IOException {
        Document document;

        try {
            document = XMLUtil.builder.parse(new ByteArrayInputStream(buffer, 0, bytesRead));
            return ResponseFactory.getResponse(document);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        // Disconnection should be performed here!!!
        return null;
    }

    /**
     * Dispatch a parsed message to the topic that is relevant for network messages.
     */
    private void dispatch() {
        ServiceDebugLogger.log(this.getClass(), "Response received from server!\n\n" + message.getTextContent() + "\n");
        eventBus.publish(new ResponseEvent((Response) message), RESPONSES_TOPIC);
        resetMarker();
    }

    @Override
    public void handle(Event event) {
        super.handle(event);
    }

    @Override
    public void run() {
        ServiceDebugLogger.log(this.getClass(), "Started!");

        while (!terminated) {
            try {
                read();
                message = parseMessageBytes();
                dispatch();
            } catch (EOFException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                initiateUnexpectedTermination(e);
            }
        }
        ServiceDebugLogger.log(this.getClass(), "Terminated!");
    }

}
