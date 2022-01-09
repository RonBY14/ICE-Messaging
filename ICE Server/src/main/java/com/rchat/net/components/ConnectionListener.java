package com.rchat.net.components;

import com.rchat.core.Initializer;
import com.rchat.net.entities.client.Client;
import com.rchat.utils.ServiceDebugLogger;
import com.rchat.utils.RunnableImpl;

public class ConnectionListener extends RunnableImpl {

    private ServerController serverController;
    private ClientHandler clientHandler;

    public ConnectionListener(ServerController serverController, ClientHandler clientHandler) {
        this.serverController = serverController;
        this.clientHandler = clientHandler;
    }

    @Override
    public void doBefore() {
        ServiceDebugLogger.log(this.getClass(), "Started!");
        running = true;
    }

    @Override
    public void doAfter() {
        ServiceDebugLogger.log(this.getClass(), "Terminated!");
    }

    public void run() {
        doBefore();

        while (running) {
            try {
                ServiceDebugLogger.log(this.getClass(), "Listening for connection requests...");

                Client client = new Client(
                        clientHandler,
                        serverController.getServerSocket().accept(),
                        (Receiver) Initializer.getBean("Receiver"));
                ServiceDebugLogger.log(this.getClass(),
                        "Connection request accepted and forwarded to the ClientHandler service.");

                clientHandler.add(client);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        doAfter();
    }
}
