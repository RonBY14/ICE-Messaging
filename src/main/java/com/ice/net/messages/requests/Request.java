package com.ice.net.messages.requests;

import com.ice.net.messages.Message;

/**
 * Represents an XML request.
 * 
 * @author Ron
 * @since 1.0
 */
public abstract class Request extends Message {

	public Request(String type) {
		super(type);
	}

	protected abstract void generate();
}
