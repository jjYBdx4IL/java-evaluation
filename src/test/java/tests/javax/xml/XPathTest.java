/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.javax.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class XPathTest {

    private static final Logger LOG = LoggerFactory.getLogger(XPathTest.class);
    
    @Test
    public void test1() throws Exception {
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
        
        Node n = (Node) xpath.evaluate("//bb", doc, XPathConstants.NODE);
        assertEquals("1", n.getAttributes().getNamedItem("id").getNodeValue());
    }
    
    @Test
    public void testNamespaces() throws Exception {
        InputStream is = new ByteArrayInputStream(svgBaseFragment.getBytes());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        assertFalse(builder.isNamespaceAware());
        Document doc = builder.parse(is);
        is.close();
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(svgCtx);

        assertNotNull(xpath.evaluate("/svg", doc, XPathConstants.NODE));
        assertNull(xpath.evaluate("/svgabc", doc, XPathConstants.NODE));
    }
    
    public static final NamespaceContext svgCtx = new NamespaceContext() {
        private final Map<String, String> map = new HashMap<>();
        {
            map.put("svg", "http://www.w3.org/2000/svg");
        }

        public String getNamespaceURI(String prefix) {
            LOG.info("getNamespaceURI({})", prefix);
            return map.get(prefix);
        }

        public String getPrefix(String uri) {
            throw new UnsupportedOperationException();
        }

        public Iterator<String> getPrefixes(String uri) {
            throw new UnsupportedOperationException();
        }
    };
    
    public static final String svgBaseFragment = "<?xml version=\"1.0\" standalone=\"no\"?>"
            + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" "
            + "\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\" ><svg "
            + "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
            + "width=\"100%\" height=\"100%\"></svg>";
}
