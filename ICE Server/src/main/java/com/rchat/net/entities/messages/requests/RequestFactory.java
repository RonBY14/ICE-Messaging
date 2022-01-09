package com.rchat.net.entities.messages.requests;

import com.rchat.net.entities.client.Client;
import org.w3c.dom.Document;

public class RequestFactory {
    
    public static Request getRequest(Document document, Client sender) {
        switch (document.getDocumentElement().getTagName()) {
            case RequestAcronym.AUTHENTICATION_REQUEST:
                return new AuthenticationRequest(document, sender);
            case RequestAcronym.DELIVER_REQUEST:
                return new DeliverRequest(document, sender);
        }
        return new BadRequest(document, sender);
    }
}
