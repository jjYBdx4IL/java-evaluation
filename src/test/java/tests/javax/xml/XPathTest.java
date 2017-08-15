/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.javax.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class XPathTest {

    private static final Logger log = LoggerFactory.getLogger(XPathTest.class);

    @Test
    public void test1() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        InputStream is = new ByteArrayInputStream("<root><aaa id='123'>abc</aaa><bb id='1'/><bb id='2'/></root>".getBytes());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();

        XPathExpression expr = xpath.compile("//aaa/text()");
        assertEquals("abc", (String) expr.evaluate(doc, XPathConstants.STRING));
        assertEquals("abc", xpath.compile("//aaa/text()").evaluate(doc));
        assertEquals("abc", xpath.compile("//aaa[@id='123']").evaluate(doc));
        assertEquals("", xpath.compile("//aaa[@id='124']").evaluate(doc));

        expr = xpath.compile("//aaa");
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        assertEquals(1, nl.getLength());
        assertEquals("123", nl.item(0).getAttributes().getNamedItem("id").getNodeValue());

        assertEquals("123", xpath.compile("//aaa/@id").evaluate(doc));

        assertEquals("1", xpath.compile("//bb/@id").evaluate(doc));
        assertEquals("1", xpath.compile("//bb[1]/@id").evaluate(doc));
        assertEquals("2", xpath.compile("//bb[2]/@id").evaluate(doc));
    }
}
