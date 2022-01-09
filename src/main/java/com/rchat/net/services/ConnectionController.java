package com.rchat.net.services;

import com.eventsystem.components.EventBus;
import com.eventsystem.events.Event;
import com.eventsystem.subscriber.Subscriber;
import com.rchat.events.net.*;
import com.rchat.events.services.NetServiceStartEvent;
import com.rchat.events.services.ServiceFailedEvent;
import com.rchat.events.services.ServiceTerminateEvent;
import com.rchat.models.DataAccess;
import com.rchat.net.exceptions.UnableToStartServiceException;
import com.rchat.net.messages.requests.AuthenticationRequest;
import com.rchat.net.messages.responses.AuthenticationResponse;
import com.rchat.net.messages.responses.Response;
import com.rchat.net.messages.responses.ResponseAcronym;
import com.rchat.net.utils.AuthenticationResult;
import com.rchat.net.utils.ServiceDebugLogger;
import com.rchat.ui.GUIManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.NotYetConnectedException;
import java.util.Date;

/**
 * Contains the application's socket and used to control the connection.
 *
 * @author Ron
 * @since 1.0
 */
public class ConnectionController extends AbstractService implements Subscriber {

    public static final int PORT = 3348;
    public static final int DEFAULT_TIMEOUT = 2000;

    public static final String CC_TOPIC = "CC";

    private Socket socket;

    public ConnectionController(EventBus eventBus, DataAccess dataAccess) {
        super(eventBus, dataAccess);

        eventBus.createTopic(CC_TOPIC);
        eventBus.subscribe(this, CC_TOPIC);
        eventBus.subscribe(this, MessageReader.RESPONSES_TOPIC);

        /*
        This socket initialization is used only to avoid null checks in other methods.
        Which means that this current object is useless and the actual Socket object
        will be created when some service will request to connect.
         */
        socket = new Socket();
    }

    /**
     * Closes the current socket (If it is not closed) and creates a new one.
     * This method is called by the connect method.
     */
    private void replaceAndConfigureNewSocket() {
        if (socket.isConnected() || !socket.isClosed())
            disconnect();
        socket = new Socket();
    }

    /**
     * Connects a socket to a server specified in the
     * ConnectEvent object that triggered this method, starts
     * the essential network services, and sends
     * authentication request to the server.
     *
     * @param event - the event object that triggered this method.
     */
    private synchronized void connect(ConnectEvent event) {
        replaceAndConfigureNewSocket();

        ServiceDebugLogger.log(this.getClass(), "Connecting...");

        try {
            socket.connect(
                    new InetSocketAddress(InetAddress.getByName(event.getHostAddress()).getHostAddress(), PORT),
                    DEFAULT_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
            eventBus.publish(new UnableToConenctEvent(e), GUIManager.GUI_TOPIC);
            return;
        }
        startNetworkServices();
        // Move the authentication to be performed only after the services started!!!!
        ServiceDebugLogger.log(this.getClass(), "Connecting... sending authentication request to the server...");
        eventBus.publish(
                new SendRequestEvent(new AuthenticationRequest(dataAccess.getUser().getNickname())),
                Sender.SENDER_TOPIC);
    }

    /**
     * Starts the essential services.
     * This method is called by connect() method.
     *
     * @throws UnableToStartServiceException - thrown if socket isn't connected.
     */
    private synchronized void startNetworkServices() throws UnableToStartServiceException {
        if (!socket.isConnected())
            throw new UnableToStartServiceException(new NotYetConnectedException());
        ServiceDebugLogger.log(this.getClass(), "Connecting... Starting network services...");

        eventBus.publish(new NetServiceStartEvent(socket), Sender.SENDER_TOPIC);
        eventBus.publish(new NetServiceStartEvent(socket), MessageReader.RECEIVER_TOPIC);
    }

    /**
     * Handles an authentication response (Authentication Result)
     * received from the server.
     *
     * @param response - the authentication response.
     */
    private synchronized void handleAuthenticationResult(AuthenticationResponse response) {
        AuthenticationResult result = response.getAuthenticationResult();

        if (result.equals(AuthenticationResult.SUCCESS)) {
            dataAccess.getSessionData().setSocket(socket);
            dataAccess.getSessionData().setConnectionStarted(new Date());
            ServiceDebugLogger.log(this.getClass(), "Connected!");
        } else {
            disconnect();
        }
        eventBus.publish(new AuthenticationEvent(result), GUIManager.GUI_TOPIC);
    }

    public synchronized void disconnect() {
        disconnect(null);
    }

    /**
     * Terminates all the working services and closes the socket.
     *
     * @param event - the event object triggered this method.
     */
    public synchronized void disconnect(DisconnectEvent event) {
        if (!socket.isClosed()) {
            terminateNetworkServices(event);

            try {
                socket.close();

                if (socket.isConnected())
                    ServiceDebugLogger.log(this.getClass(), "Disconnected from server!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Terminates the essential services.
     * This method is called by disconnect() method.
     */
    private synchronized void terminateNetworkServices(DisconnectEvent event) {
        eventBus.publish(new ServiceTerminateEvent(), Sender.SENDER_TOPIC);
        eventBus.publish(new ServiceTerminateEvent(), MessageReader.RECEIVER_TOPIC);

        if (event != null)
            eventBus.publish(new DisconnectedEvent(DisconnectedEvent.DisconnectionReason.REQUESTED), GUIManager.GUI_TOPIC);
        else
            eventBus.publish(new DisconnectedEvent(DisconnectedEvent.DisconnectionReason.CONNECTION_LOST), GUIManager.GUI_TOPIC);
    }

    /**
     * Network services may fail to work, mainly because of
     * connection issues. This method handles cases of failed
     * service.
     */
    public void handleFailedService() {
        disconnect();
    }

    @Override
    public void handle(Event event) {
        if (event instanceof ConnectEvent) {
            connect((ConnectEvent) event);
        } else if (event instanceof DisconnectEvent) {
            disconnect((DisconnectEvent) event);
        } else if (event instanceof ResponseEvent) {
            Response response = ((ResponseEvent) event).getResponse();

            if (response.getType().equals(ResponseAcronym.AUTHENTICATION_RESPONSE))
                handleAuthenticationResult((AuthenticationResponse) response);
        } else if (event instanceof ServiceFailedEvent) {
            handleFailedService();
        }
    }
}
