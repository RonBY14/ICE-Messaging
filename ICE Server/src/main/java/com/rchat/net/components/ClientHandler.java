package com.rchat.net.components;

import com.rchat.net.entities.client.Client;
import com.rchat.net.entities.messages.responses.AlignResponse;
import com.rchat.utils.ServiceDebugLogger;
import com.rchat.utils.RunnableImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class ClientHandler extends RunnableImpl {

    public static final int ENFORCER_CHECKS = 500;
    public static final int VERIFICATION_TIMEOUT = 40_000;

    private final Distributor distributor;

    private final Hashtable<String, Client> verifiedClients;
    private final ArrayList<Client> unverifiedClients;

    public ClientHandler(Distributor distributor) {
        this.distributor = distributor;

        verifiedClients = new Hashtable<>();
        unverifiedClients = new ArrayList<>();
    }

    public Hashtable<String, Client> getVerifiedClients() {
        return verifiedClients;
    }

    public Client[] getVerifiedClientsArray() {
        return verifiedClients.values().toArray(new Client[0]);
    }

    public ArrayList<Client> getUnverifiedClients() {
        return unverifiedClients;
    }

    public Client getClient(@NotNull String name) {
        return verifiedClients.get(name);
    }

    public void add(@NotNull Client client) {
        unverifiedClients.add(client);
    }

    public void authenticate(@NotNull Client client) {
        if (client.isAuthenticated()) {
            unverifiedClients.remove(client);
            verifiedClients.put(client.getNickname(), client);
        }
    }

    public void kick(@NotNull String name) {
        closeClientSocket(verifiedClients.remove(name));
        ServiceDebugLogger.log(this.getClass(), name + " Kicked from the server.");
    }

    public void kickUnverifiedClient(@NotNull Client client) {
        unverifiedClients.remove(client);
        closeClientSocket(client);
        ServiceDebugLogger.log(this.getClass(), client.getNickname() + " client kicked from the server.");
    }

    private void closeClientSocket(@NotNull Client client) {
        try {
            client.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        distributor.push(new AlignResponse(getVerifiedClientsArray(), getVerifiedClientsArray()));
    }

    private void enforce() {
        try {
            Thread.sleep(ENFORCER_CHECKS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (unverifiedClients.size() > 0)
            checkUnverifiedClients();
        checkClientsServices();
    }

    private void checkUnverifiedClients() {
        ServiceDebugLogger.log(this.getClass(), "Trying to verify clients...");

        for (int i = 0; i < unverifiedClients.size(); i++) {
            Client client = unverifiedClients.get(i);

            if (System.currentTimeMillis() - client.getConnectionStarted() > VERIFICATION_TIMEOUT) {
                ServiceDebugLogger.log(this.getClass(), "VERIFICATION TIMEOUT OCCURRED!");
                kickUnverifiedClient(client);
            }
        }
    }

    private void checkClientsServices() {
        Client[] clients = getVerifiedClientsArray();

        for (int i = 0; i < clients.length; i++) {
            Client client = clients[i];

            if (!client.getSocket().isConnected() || client.getSocket().isClosed()) {
                kick(client.getNickname());
            } else if (!client.getReceiver().isRunning()) {
                System.out.println("Trying to fix " + client.getNickname() + "'s receiver service...");
                client.getReceiver().start();

                if (!client.getReceiver().isRunning()) {
                    System.out.println(client.getNickname() + "'s receiver service failed! { USELESS CONNECTION }");
                    kick(client.getNickname());
                }
            }
        }
    }

    public boolean nicknameExists(String name) {
        Client[] clients = getVerifiedClientsArray();

        for (int i = 0; i < clients.length; i++) {
            Client client = clients[i];

            if (name.equals(client.getNickname()))
                return true;
        }
        return false;
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

    @Override
    public void run() {
        doBefore();

        while (running)
            enforce();
        doAfter();
    }
}
