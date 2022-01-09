package com.ice.models;

import com.ice.net.services.ConnectionController;

import java.net.Socket;
import java.util.Date;

public class SessionData {

    private Socket socket;

    private Date connectionStarted;

    private int bytesReceived;
    private int bytesSent;

    public SessionData() {}

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getHostAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public void setConnectionStarted(Date connectionStarted) {
        this.connectionStarted = connectionStarted;
    }

    public void addBytesReceived(int amount) {
        bytesReceived += amount;
    }

    public void addBytesSent(int amount) {
        bytesSent += amount;
    }

    public void resetTrafficData() {
        bytesReceived = 0;
        bytesSent = 0;
    }

    public String toString() {
        return "Connected To: " + getHostAddress() + ":" + ConnectionController.PORT +
                "\nStarted At: " + connectionStarted +
                "\n\nBytes Received: " + bytesReceived +
                "\nBytes Sent: " + bytesSent;
    }
}
