package com.rchat.net.messages.requests;

import com.rchat.net.messages.Message;

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
