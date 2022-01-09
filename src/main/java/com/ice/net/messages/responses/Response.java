package com.ice.net.messages.responses;

import com.ice.net.messages.Message;
import org.w3c.dom.Document;

/**
 * Represents an XML response.
 * 
 * @author Ron
 * @since 1.0
 */
public abstract class Response extends Message {

	private int handlingFailures;

	public Response(Document document) {
		super(document);
	}

	public int getHandlingFailures() {
		return handlingFailures;
	}

	public void addDispatchFailures(int amount) {
		handlingFailures += amount;
	}
}
