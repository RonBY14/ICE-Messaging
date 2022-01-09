package com.rchat.net.entities.messages.responses;

import com.rchat.net.entities.client.Client;
import org.w3c.dom.Element;

public class MessageResponse extends Response {

    private String from;

    private String message;

    public MessageResponse(Client[] recipients, String from, String message) {
        super(ResponseAcronym.MESSAGE_RESPONSE, recipients);

        this.from = from;
        this.message = message;

        generate();
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void generate() {
        Element root = document.getDocumentElement();

        Element from = document.createElement("from");
        from.setTextContent(this.from);
        Element message = document.createElement("message");
        message.setTextContent(this.message);

        root.appendChild(from);
        root.appendChild(message);

        applyNewLengthCalculation();
    }
}
