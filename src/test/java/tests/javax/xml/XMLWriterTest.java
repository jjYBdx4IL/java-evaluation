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
import java.io.CharArrayReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.apache.xerces.impl.Constants;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings({ "deprecation" })
public class XMLWriterTest {

    private final static Logger LOG = Logger.getLogger(XMLWriterTest.class.getName());

    /**
     * javax.xml.* does not support pretty-printing (or it is at least dependent on external implementations).
     */
    @Test
    public void testW3CDoc2XMLStringConversion() {
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><sub><text>&lt;&gt;</text></sub></root>",
                transform(getW3CDocument1()));
    }

    static String transform(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, result);
            return writer.toString().replace("\r", "");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Document getW3CDocument1() {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = doc.createElement("root");
            Element subElement = doc.createElement("sub");
            Element textElement = doc.createElement("text");
            textElement.appendChild(doc.createTextNode("<>"));
            subElement.appendChild(textElement);
            rootElement.appendChild(subElement);
            doc.appendChild(rootElement);
            return doc;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void testPrettyPrint() {
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?><root>" + System.lineSeparator()
                + "   <sub>" + System.lineSeparator()
                + "      <text>&lt;&gt;</text>" + System.lineSeparator()
                + "   </sub>" + System.lineSeparator()
                + "</root>" + System.lineSeparator(),
                transformPretty(getW3CDocument1()));
    }

	private String transformPretty(Document document) {
        try {
            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter(Constants.DOM_FORMAT_PRETTY_PRINT, Boolean.TRUE); // Set this to true if the output needs to be beautified.

            return writer.writeToString(document);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Document fromXMLString(String xml) {
        try {
            final InputSource src = new InputSource(new StringReader(xml));
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatXml(String xml) {
        try {
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            //serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            //serializer.setOutputProperty("{http://xml.customer.org/xslt}indent-amount", "2");
            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String prettyPrintWithApacheXML(Document document) {
        try {
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String prettyPrintWithXercesDomLevel3(byte[] input) {
        try {
            //System.setProperty(DOMImplementationRegistry.PROPERTY,"org.apache.xerces.dom.DOMImplementationSourceImpl");
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("XML 3.0 LS 3.0");
            if (impl == null) {
                throw new RuntimeException("No DOMImplementation found !");
            }

            LOG.info(String.format("DOMImplementationLS: %s", impl.getClass().getName()));

            LSParser parser = impl.createLSParser(
                    DOMImplementationLS.MODE_SYNCHRONOUS,
                    //"http://www.w3.org/2001/XMLSchema");
                    "http://www.w3.org/TR/REC-xml");
            LOG.info(String.format("LSParser: %s", parser.getClass().getName()));
            LSInput lsi = impl.createLSInput();
            lsi.setByteStream(new ByteArrayInputStream(input));
            Document doc = parser.parse(lsi);

            LSSerializer serializer = impl.createLSSerializer();
            serializer.getDomConfig().setParameter("format-pretty-print",Boolean.TRUE);
            LSOutput output = impl.createLSOutput();
            output.setEncoding("UTF-8");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            output.setByteStream(baos);
            serializer.write(doc, output);
            return baos.toString();
//            return serializer.writeToString(doc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Document buildDocument(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setValidating(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            CharArrayReader reader = new CharArrayReader(xml.toCharArray());
            InputSource is = new InputSource(reader);
            return db.parse(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String prettyPrintWithXercesDomLevel3(Document doc) {
        try {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("XML 3.0 LS 3.0");
            if (impl == null) {
                throw new RuntimeException("No DOMImplementation found !");
            }

            LOG.info(String.format("DOMImplementationLS: %s", impl.getClass().getName()));

            LSParser parser = impl.createLSParser(
                    DOMImplementationLS.MODE_SYNCHRONOUS,
                    "http://www.w3.org/TR/REC-xml");
            // http://www.w3.org/2001/XMLSchema
            LOG.info(String.format("LSParser: %s", parser.getClass().getName()));

            LSSerializer serializer = impl.createLSSerializer();
            for(int i=0; i<serializer.getDomConfig().getParameterNames().getLength(); i++) {
                LOG.info(serializer.getDomConfig().getParameterNames().item(i));
            }
            serializer.getDomConfig().setParameter(Constants.DOM_FORMAT_PRETTY_PRINT, Boolean.TRUE);
            
            LSOutput output = impl.createLSOutput();
            output.setEncoding("UTF-8");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            output.setByteStream(baos);
            serializer.write(doc, output);
            return baos.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPrettyPrintConvert() {
        String unformattedXml
                = "<?xml version=\"1.0\" encoding=\"UTF-16\"?><QueryMessage\n"
                + "        xmlns=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message\"\n"
                + "        xmlns:query=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query\">\n"
                + "    <Query>\n"
                + "        <query:CategorySchemeWhere>\n"
                + "   \t\t\t\t\t         <query:AgencyID>ECB\n\n\n\n</query:AgencyID>\n"
                + "        </query:CategorySchemeWhere>\n"
                + "    </Query>\n\n\n\n\n"
                + "</QueryMessage>";

        System.out.println(prettyPrintWithApacheXML(fromXMLString(unformattedXml)));
    }

    @Test
    public void testPrettyPrintConvertDomLevel3() throws UnsupportedEncodingException {
        String unformattedXml
                = "<?xml version=\"1.0\" encoding=\"UTF-16\"?><QueryMessage\n"
                + "        xmlns=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message\"\n"
                + "        xmlns:query=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query\">\n"
                + "    <Query>\n"
                + "        <query:CategorySchemeWhere>\n"
                + "   \t\t\t\t\t         <query:AgencyID>ECB\n\n\n\n</query:AgencyID>\n"
                + "        </query:CategorySchemeWhere>\n"
                + "    </Query>\n\n\n\n\n"
                + "</QueryMessage>";

        System.out.println(prettyPrintWithXercesDomLevel3(unformattedXml.getBytes("UTF-16")));
    }
    @Test
    public void testPrettyPrintConvertDomLevel3utf8() {
        String unformattedXml
                = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><QueryMessage\n"
                + "        xmlns=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message\"\n"
                + "        xmlns:query=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query\">\n"
                + "    <Query>\n"
                + "        <query:CategorySchemeWhere>\n"
                + "   \t\t\t\t\t         <query:AgencyID>ECB\n\n\n\n</query:AgencyID>\n"
                + "        </query:CategorySchemeWhere>\n"
                + "    </Query>\n\n\n\n\n"
                + "</QueryMessage>";

        System.out.println(prettyPrintWithXercesDomLevel3(unformattedXml.getBytes()));
    }
    @Test
    public void testPrettyPrintConvertDomLevel3b() {
        String unformattedXml
                = "<?xml version=\"1.0\" encoding=\"UTF-16\"?><QueryMessage\n"
                + "        xmlns=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message\"\n"
                + "        xmlns:query=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query\">\n"
                + "    <Query>\n"
                + "        <query:CategorySchemeWhere>\n"
                + "   \t\t\t\t\t         <query:AgencyID>ECB\n\n\n\n</query:AgencyID>\n"
                + "        </query:CategorySchemeWhere>\n"
                + "    </Query>\n\n\n\n\n"
                + "</QueryMessage>";

        System.out.println(prettyPrintWithXercesDomLevel3(buildDocument(unformattedXml)));
    }
}
