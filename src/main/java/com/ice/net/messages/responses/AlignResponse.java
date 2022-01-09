package com.ice.net.messages.responses;

import org.w3c.dom.Document;

public class AlignResponse extends Response {

    private String[] participantsList;

    public AlignResponse(Document document) {
        super(document);

         participantsList = document.getElementsByTagName("plst").item(0).getTextContent().split(", ");
    }

    public String[] getParticipantsList() {
        return participantsList;
    }
}
