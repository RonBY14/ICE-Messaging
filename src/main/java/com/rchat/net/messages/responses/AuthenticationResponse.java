package com.rchat.net.messages.responses;

import com.rchat.net.utils.AuthenticationResult;
import org.w3c.dom.Document;

public class AuthenticationResponse extends Response {

    private AuthenticationResult authenticationResult;

    public AuthenticationResponse(Document document) {
        super(document);

        authenticationResult =
                AuthenticationResult.valueOf(document.getElementsByTagName("result").item(0).getTextContent().toUpperCase());
    }

    public AuthenticationResult getAuthenticationResult() {
        return authenticationResult;
    }

    public boolean succeed() {
        if (authenticationResult.equals(AuthenticationResult.SUCCESS))
            return true;
        return false;
    }
}
