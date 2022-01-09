package com.ice.net.messages.requests;

import com.ice.net.utils.RoutingScheme;
import org.w3c.dom.Element;

public class DeliverRequest extends Request {

    private String message;
    private RoutingScheme routingScheme;

    public DeliverRequest(String message, RoutingScheme routingScheme) {
        super(RequestAcronym.DELIVER_REQUEST);

        this.message = message.strip();
        this.routingScheme = routingScheme;

        generate();
    }

    public String getMessage() {
        return message;
    }

    public RoutingScheme getRoutingScheme() {
        return routingScheme;
    }

    protected void generate() {
        Element root = document.getDocumentElement();

        Element scheme = document.createElement("scheme");
        scheme.setAttribute("mode", routingScheme.name().toLowerCase());
        root.appendChild(scheme);

        Element message = document.createElement("message");
        message.setTextContent(this.message);
        root.appendChild(message);

        applyNewLengthCalculation();
    }
}
