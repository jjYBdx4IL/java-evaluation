package org.apache.batik;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class SvgParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(SvgParserTest.class);
    
    private static final boolean prettyPrint = true;

    public static final String svgBaseFragment = "<?xml version=\"1.0\" standalone=\"no\"?>"
            + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" "
            + "\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\" ><svg "
            + "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
            + "width=\"100%\" height=\"100%\"></svg>";

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

    @Test
    public void test() throws Exception {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        // this factory seems to be namespace-aware, so we *have* to use ns prefixes in our xpath expressions
        // or we won't match any elements of the document's default namespace.

        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(svgCtx);
        
        SVGDocument doc;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(svgBaseFragment.getBytes(UTF_8));
                InputStreamReader isr = new InputStreamReader(bais)) {
            doc = f.createSVGDocument("", isr);
        }

        assertNotNull(xPath.evaluate("/svg:svg", doc, XPathConstants.NODE));
        assertNull(xPath.evaluate("/svg", doc, XPathConstants.NODE));

        trimWhitespace((Node) xPath.evaluate("/svg:svg", doc, XPathConstants.NODE));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        if (prettyPrint) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Result output = new StreamResult(baos);
        Source input = new DOMSource(doc);
        transformer.transform(input, output);
        LOG.info(baos.toString(UTF_8));
    }
    
    public static void trimWhitespace(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                if (child.getNodeValue().isBlank()) {
                    child.setNodeValue("");
                }
            }
            trimWhitespace(child);
        }
    }
    
    public static Node[] toArray(NodeList nodeList) {
        Node[] nodes = new Node[nodeList.getLength()];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = nodeList.item(i);
        }
        return nodes;
    }
}
