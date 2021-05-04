package tests.javax.xml;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class XmlEditTest {

    private static final Logger LOG = LoggerFactory.getLogger(XmlEditTest.class);

    @SuppressWarnings("unused")
	@Test
    public void test() throws SAXException, IOException, ParserConfigurationException,
            TransformerConfigurationException, TransformerException {
        String inputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?> \n"
                + "<company>\n"
                + "   <staff id=\"1\">\n"
                + "     <firstname>yong</firstname>\n"
                + "     <lastname>mook kim</lastname>\n"
                + "     <nickname>mkyong</nickname>\n"
                + "     <salary>100000</salary>\n"
                + "   </staff>\n"
                + "</company>\n";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new ByteArrayInputStream(inputXml.getBytes()));

        Node company = doc.getFirstChild();
        Node staff = doc.getElementsByTagName("staff").item(0);

        // update staff attribute
        NamedNodeMap attr = staff.getAttributes();
        Node nodeAttr = attr.getNamedItem("id");
        nodeAttr.setTextContent("2");

        // append a new node to staff
        Element age = doc.createElement("age");
        age.appendChild(doc.createTextNode("28"));
        staff.appendChild(age);

        // loop the staff child node
        NodeList list = staff.getChildNodes();

        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);

            // get the salary element, and update the value
            if ("salary".equals(node.getNodeName())) {
                node.setTextContent("2000000");
            }

            //remove firstname
            if ("firstname".equals(node.getNodeName())) {
                staff.removeChild(node);
            }
        }

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(baos);
        transformer.transform(source, result);
        String outputXml = baos.toString();

        LOG.info("input:");
        LOG.info(inputXml);
        LOG.info("output:");
        LOG.info(outputXml);

        assertTrue(inputXml.contains("firstname"));
        assertFalse(outputXml.contains("firstname"));
    }
}
