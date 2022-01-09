package com.rchat.net.entities.messages.requests;

import com.rchat.net.entities.client.Client;
import org.w3c.dom.Document;

public class AuthenticationRequest extends Request {

    private String nickname;

    public AuthenticationRequest(Document document, Client sender) {
        super(document, sender);

        nickname = document.getElementsByTagName("nickname").item(0).getTextContent();
    }

    public String getNickname() {
        return nickname;
    }

}
