package com.rchat.net.entities.messages.requests;

import com.rchat.net.entities.client.Client;
import org.w3c.dom.Document;

public class AlignRequest extends Request {

    public AlignRequest(Document document, Client sender) {
        super(document, sender);
    }
}
