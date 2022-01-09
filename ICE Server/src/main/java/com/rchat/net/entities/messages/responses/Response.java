package com.rchat.net.entities.messages.responses;

import com.rchat.net.entities.client.Client;
import com.rchat.net.entities.messages.requests.Message;

/**
 * Represents an XML response.
 * 
 * @author Ron
 * @since 1.0
 */
public abstract class Response extends Message {

	protected Client[] recipients;

	public Response(String type, Client[] recipients) {
		super(type);

		assert recipients != null : "Recipients can't be NULL!";

		this.recipients = recipients;
	}

	public Client[] getRecipients() {
		return recipients;
	}

	protected abstract void generate();
}
