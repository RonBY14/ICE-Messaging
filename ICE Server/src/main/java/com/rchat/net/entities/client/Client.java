package com.rchat.net.entities.client;

import com.rchat.net.components.ClientHandler;
import com.rchat.net.components.Receiver;
import com.rchat.net.utils.AuthenticationResult;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.net.Socket;

public class Client {

    public static final String DEFAULT_NAME = "unverified";

    private ClientHandler clientHandler;

    private Socket socket;
    private Receiver receiver;

    private long connectionStarted;

    private boolean authenticated;

    private String nickname;

    public Client(@NotNull ClientHandler clientHandler, @NotNull Socket socket, @NotNull Receiver receiver) {
        this.clientHandler = clientHandler;

        this.socket = socket;
        this.receiver = receiver;
        receiver.setOwner(this);
        receiver.start();

        connectionStarted = System.currentTimeMillis();

        nickname = DEFAULT_NAME;

        authenticated = false;
    }

    public Socket getSocket() {
        return socket;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public long getConnectionStarted() {
        return connectionStarted;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getNickname() {
        return nickname;
    }

    public AuthenticationResult setNickname(@NotNull String nickname) {
        String invalidCharacters = ":;@?/\\`!(){}^%$#+-*.,'\"[]";

        if (nickname.isEmpty() || nickname.isBlank()) {
            return AuthenticationResult.BLANK_OR_EMPTY;
        } else if (nickname.length() < 2) {
            return AuthenticationResult.TOO_SHORT;
        } else if (nickname.length() > 12) {
            return AuthenticationResult.TOO_LONG;
        } else if (StringUtils.containsAny(nickname, invalidCharacters)) {
            return AuthenticationResult.INVALID;
        } else if (clientHandler.nicknameExists(nickname) || nickname.equals(DEFAULT_NAME) || nickname.equalsIgnoreCase("Me")) {
            return AuthenticationResult.UNAVAILABLE;
        } else {
            this.nickname = nickname;
        }
        return AuthenticationResult.SUCCESS;
    }

    public boolean authenticate() {
        if (!nickname.equals(DEFAULT_NAME)) {
            authenticated = true;
            clientHandler.authenticate(this);
            return true;
        }
        return false;
    }

}
