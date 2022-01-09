package com.rchat.net.entities.messages.requests;

import com.rchat.net.entities.client.Client;
import org.w3c.dom.Document;

/**
 * Represents an XML request.
 * 
 * @author Ron
 * @since 1.0
 */
public abstract class Request extends Message {

	private Client sender;

	public Request(Document document, Client sender) {
		super(document);

		assert sender != null : "Sender can't be NULL!";

		this.sender = sender;
	}

	public Client getSender() {
		return sender;
	}

}
