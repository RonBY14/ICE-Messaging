package com.rchat.net.components;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Contains the program's socket and generally controls the connection.
 *
 * @author Ron
 * @since 1.0
 */
public class ServerController {

    private ServerSocket serverSocket;

    public ServerController() {
        try {
            serverSocket = new ServerSocket(3348);
        } catch (IOException e) {
            System.exit(1);
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

}
