package com.rchat.net.components;

import com.rchat.net.entities.client.Client;
import com.rchat.net.entities.messages.exceptions.MessageOversizeException;
import com.rchat.net.entities.messages.requests.BadRequest;
import com.rchat.net.entities.messages.requests.Request;
import com.rchat.net.entities.messages.requests.RequestFactory;
import com.rchat.net.utils.ProtocolConstants;
import com.rchat.utils.RunnableImpl;
import com.rchat.utils.ServiceDebugLogger;
import com.rchat.utils.XMLUtil;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Responsible for receiving and parsing requests
 * incoming from the receiver's owner socket.
 *
 * @author Ron
 * @since 1.0
 */
public class Receiver extends RunnableImpl {

    /**
     * The capacity of the receiver's data buffer (in bytes).
     *
     * @see #buffer
     * @see #bytesRead
     */
    public final int CAPACITY = 65536;

    /** Service that handles received-parsed requests. */
    private final RequestHandler requestHandler;

    /**
     * The client that this receiver is allocated for (owned by the client).
     * The receiver listen and read from its owner's socket input stream.
     */
    private Client owner;

    /** Buffers incoming requests bytes (In-Order) */
    private final byte[] buffer;
    /** Number of bytes read from a single request to the buffer */
    private int bytesRead;

    /** Connected to the client's socket input stream. */
    private DataInputStream inputStream;

    public Receiver(@NotNull RequestHandler requestHandler){
        this.requestHandler = requestHandler;

        buffer = new byte[CAPACITY];
        bytesRead = 0;

        inputStream = null;
    }

    public Client getOwner() {
        return owner;
    }

    public void setOwner(Client owner) {
        this.owner = owner;
    }

    private int findMessageLength() throws IOException, NumberFormatException, MessageOversizeException {
        for (int i = 0; i < ProtocolConstants.LENGTH_ATTR_INDEX + ProtocolConstants.HEADER_ATTRS_LENGTH; i++)
            buffer[bytesRead++] = inputStream.readByte();
        int length = Integer.parseInt(new String(Arrays.copyOfRange(buffer, ProtocolConstants.LENGTH_ATTR_INDEX, bytesRead)));

        if (length > CAPACITY)
            throw new MessageOversizeException(length, CAPACITY);
        return length;
    }

    private void completeMessageReading(int length) throws IOException {
        while (bytesRead < length)
            buffer[bytesRead++] = inputStream.readByte();
    }

    private Request parseMessageBytes() throws IOException {
        Document document;

        try {
            document = XMLUtil.builder.parse(new ByteArrayInputStream(buffer, 0, bytesRead));
            return RequestFactory.getRequest(document, owner);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return new BadRequest(null, owner);
    }

    /**
     * Read bytes from client's socket.
     *
     * @throws Exception
     */
    private void receive() throws IOException {
        try {
            // Locating and parsing the message's length
            int length = findMessageLength();
            completeMessageReading(length);

            Request request = parseMessageBytes();
            ServiceDebugLogger.log(this.getClass(), "Request from " + owner.getNickname() + " received!");
            ServiceDebugLogger.log(this.getClass(), request.getTextContent());

            // Pushing/Forwarding the message to the message handler's queue for further operations
            requestHandler.push(request);
        } catch (NumberFormatException | MessageOversizeException e) {
            e.printStackTrace();

            requestHandler.push(new BadRequest(null, owner));
            running = false;
        }
    }

    private void resetMarker() {
        bytesRead = 0;
    }

    @Override
    public void doBefore() {
        assert owner != null : "[FATAL] Receiver can't start without an owner [owner=null]!";

        try {
            inputStream = new DataInputStream(owner.getSocket().getInputStream());
            running = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        ServiceDebugLogger.log(this.getClass(), "Unverified client's receiver service started!");
    }

    @Override
    public void doAfter() {
        ServiceDebugLogger.log(this.getClass(), "Cleaning resources and resetting the buffer's marker...");

        clean(inputStream);
        resetMarker();

        ServiceDebugLogger.log(this.getClass(), "Of " + owner.getNickname() + " terminated!");
    }

    @Override
    public void run() {
        doBefore();

        while (running) {
            try {
                receive();
                resetMarker();
            } catch (Exception e) {
                e.printStackTrace();

                ServiceDebugLogger.log(this.getClass(), "Problem occurred causing termination of the Service!");
                running = false;
            }
        }
        doAfter();
    }

}
