package com.rchat.net.entities.messages.responses;

import com.rchat.net.entities.client.Client;

import com.rchat.net.utils.AuthenticationResult;
import org.w3c.dom.Element;

public class AuthenticationResponse extends Response {

    private AuthenticationResult authenticationResult;

    public AuthenticationResponse(Client[] recipients, AuthenticationResult authenticationResult) {
        super(ResponseAcronym.AUTHENTICATION_RESPONSE, recipients);

        this.authenticationResult = authenticationResult;

        generate();
    }

    @Override
    protected void generate() {
        Element root = document.getDocumentElement();

        Element result = document.createElement("result");
        result.setTextContent(authenticationResult.name().toUpperCase());

        root.appendChild(result);

        applyNewLengthCalculation();
    }
}
