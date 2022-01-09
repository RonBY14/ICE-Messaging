package com.rchat.net.messages.responses;

import org.w3c.dom.Document;

public class ResponseFactory {
    
    public static Response getResponse(Document document) {
        switch (document.getDocumentElement().getTagName()) {
            case ResponseAcronym.AUTHENTICATION_RESPONSE:
                return new AuthenticationResponse(document);
            case ResponseAcronym.ALIGN_RESPONSE:
                return new AlignResponse(document);
            case ResponseAcronym.MESSAGE_RESPONSE:
                return new MessageResponse(document);
            case ResponseAcronym.BAD_REQUEST_RESPONSE:
                return new BadRequestResponse(document);
        }
        return null;
    }
}
