package tests.javax.xml.stream;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterTest {

    @Test
    public void testGenerateXHTML() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xml = outputFactory.createXMLStreamWriter(baos);

        xml.writeStartDocument();
        xml.writeStartElement("html");
        xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");

        xml.writeStartElement("head");
        xml.writeStartElement("title");
        xml.writeCharacters("The title of the page<");
        xml.writeEndElement();
        xml.writeEndElement();

        xml.writeEndElement();
        xml.writeEndDocument();
        
        assertEquals("<?xml version='1.0' encoding='UTF-8'?><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>The title of the page&lt;</title></head></html>",
                baos.toString("UTF-8"));
    }
}
