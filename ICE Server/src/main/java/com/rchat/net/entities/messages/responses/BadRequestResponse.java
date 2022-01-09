package com.rchat.net.entities.messages.responses;

import com.rchat.net.entities.client.Client;

public class BadRequestResponse extends Response {

    public BadRequestResponse(Client recipient) {
        super(ResponseAcronym.BAD_REQUEST_RESPONSE, new Client[] { recipient });
    }

    protected void generate() { }
}
