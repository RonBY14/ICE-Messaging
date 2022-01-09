package com.ice.net.messages.responses;

import org.w3c.dom.Document;

public class MessageResponse extends Response {

    private String from;

    private String message;

    public MessageResponse(Document document) {
        super(document);

        from = document.getElementsByTagName("from").item(0).getTextContent();
        message = document.getElementsByTagName("message").item(0).getTextContent();
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }
}
