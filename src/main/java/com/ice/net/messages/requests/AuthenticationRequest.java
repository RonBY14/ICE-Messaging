package com.ice.net.messages.requests;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

public class AuthenticationRequest extends Request {

    private String nickname;

    public AuthenticationRequest(@NotNull String nickname) {
        super(RequestAcronym.AUTHENTICATION_REQUEST);

        this.nickname = nickname;

        generate();
    }

    public String getNickname() {
        return nickname;
    }

    protected void generate() {
        Element root = document.getDocumentElement();

        Element nickname = document.createElement("nickname");
        nickname.setTextContent(this.nickname);
        root.appendChild(nickname);

        applyNewLengthCalculation();
    }
}
