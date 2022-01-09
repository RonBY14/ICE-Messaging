package com.rchat.net.entities.messages.requests;

import com.rchat.net.utils.ProtocolConstants;
import com.rchat.utils.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Wraps an XML DOM Document object, that represents a message.
 * This is a convenience class for and application's communication protocol operations.
 *
 * @author Ron
 * @since 1.0
 */
public class Message {

	// XML document represents a message
	protected Document document;

	public Message(String type) {
		assert type != null : "MessageType can't be NULL!";

		generateHeader(type);
	}

	public Message(Document document) {
		this.document = document;
	}

	public String getType() {
		return document.getDocumentElement().getTagName();
	}

	public int getID() {
		return Integer.parseInt(document.getDocumentElement().getAttribute("id"));
	}

	public void setID(int id) {
		int missingDigits = ProtocolConstants.HEADER_ATTRS_LENGTH - String.valueOf(id).length();

		if (missingDigits < 0)
			return;
		Element root = document.getDocumentElement();
		root.setAttribute("id", String.join("", Collections.nCopies(missingDigits, "0")) + id);

		applyNewLengthCalculation();
	}

	public int getLength() {
		return Integer.parseInt(document.getDocumentElement().getAttribute("len"));
	}

	private void setLength(int length) {
		int missingDigits = ProtocolConstants.HEADER_ATTRS_LENGTH - String.valueOf(length).length();

		if (missingDigits < 0)
			return;
		Element root = document.getDocumentElement();
		root.setAttribute("len", String.join("", Collections.nCopies(missingDigits, "0")) + length);
	}

	/**
	 * Transforming the DOM document's text content to a java string.
	 *
	 * @return message's text content.
	 */
	public String getTextContent() {
		StringWriter writer = new StringWriter();

		try {
			XMLUtil.transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the bytes make up the message's text content.
	 *
	 * @return text content bytes.
	 */
	public byte[] getTextBytes() {
		return getTextContent().getBytes(StandardCharsets.UTF_8);
	}

	private void generateHeader(String type) {
		document = XMLUtil.builder.newDocument();

		Element root = document.createElement(type);
		document.appendChild(root);

		setID(0);
		setLength(0);

		applyNewLengthCalculation();
	}

	/**
	 * Calculates the length of the message, which is the
	 * number of bytes in UTF-8 that make up the message.
	 */
	public void applyNewLengthCalculation() {
		Element root = document.getDocumentElement();

		int totalLength = getTextBytes().length;
		setLength(totalLength);
	}

}
