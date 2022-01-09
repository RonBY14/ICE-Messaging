package com.rchat.net.entities.messages.responses;

import com.rchat.net.entities.client.Client;
import org.w3c.dom.Element;

public class AlignResponse extends Response {

    private Client[] participantsList;

    public AlignResponse(Client[] recipients, Client[] participantsList) {
        super(ResponseAcronym.ALIGN_RESPONSE, recipients);

        assert participantsList != null : "ParticipantsList can't be NULL!";
        this.participantsList = participantsList;

        generate();
    }

    public Client[] getParticipantsList() {
        return participantsList;
    }

    @Override
    protected void generate() {
        Element root = document.getDocumentElement();

        Element plst = document.createElement("plst");

        if (participantsList.length > 0) {
            String format = "";

            for (int i = 0; i < participantsList.length; i++)
                format += participantsList[i].getNickname() + ", ";
            plst.setTextContent(format.substring(0, format.length() - 2));
        }
        root.appendChild(plst);

        applyNewLengthCalculation();
    }
}
