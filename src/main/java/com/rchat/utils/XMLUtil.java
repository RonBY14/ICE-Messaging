package com.rchat.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

public class XMLUtil {

    public static DocumentBuilder builder;
    public static Transformer transformer;

    static {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            transformer = TransformerFactory.newDefaultInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            e.printStackTrace();

            System.exit(1);
        }
    }
}
