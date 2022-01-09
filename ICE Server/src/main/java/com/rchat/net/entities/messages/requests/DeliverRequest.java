package com.rchat.net.entities.messages.requests;

import com.rchat.net.entities.client.Client;
import com.rchat.net.utils.RoutingScheme;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DeliverRequest extends Request {

    private String message;
    private RoutingScheme routingScheme;

    public DeliverRequest(Document document, Client sender) {
        super(document, sender);

        message = document.getElementsByTagName("message").item(0).getTextContent();
        routingScheme =
                RoutingScheme.valueOf(
                        ((Element) document.getElementsByTagName("scheme").item(0)).
                        getAttribute("mode").toUpperCase());
    }

    public String getMessage() {
        return message;
    }

    public RoutingScheme getRoutingScheme() {
        return routingScheme;
    }

}
