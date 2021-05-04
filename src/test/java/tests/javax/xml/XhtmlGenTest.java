package tests.javax.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XhtmlGenTest {

    @Test
    public void test() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Element htmlElement = doc.createElement("html");
        htmlElement.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
        doc.appendChild(htmlElement);

        Element headElement = doc.createElement("head");
        htmlElement.appendChild(headElement);

        Element titleElement = doc.createElement("title");
        titleElement.appendChild(doc.createTextNode("The title of the page &"));
        headElement.appendChild(titleElement);

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "<head>\n"
                + "<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<title>The title of the page &amp;</title>\n"
                + "</head>\n"
                + "</html>\n",
                transform(doc));
    }

    static String transform(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString().replace("\r", "");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
