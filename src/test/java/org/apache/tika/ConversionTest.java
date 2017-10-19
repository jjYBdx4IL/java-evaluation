package org.apache.tika;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;

import java.io.ByteArrayInputStream;

public class ConversionTest {

    public static final String HTML_EXAMPLE = "<!DOCTYPE html><html>\n" +
        "<head>\n" +
        "<title> \n" +
        " A Simple HTML Document\n" +
        "</title>\n" +
        "<meta name=\"robots\" content=\"noindex,noarchive\">\n" +
        "<meta name=\"keywords\" content=\"abc,def\">\n" +
        "</head>\n" +
        "<body></div>\n" +
        "<p>This is a very simple HTML document</p>\n" +
        "<p>It only has two paragraphs &ouml;</p>\n" +
        "</body>\n" +
        "</html>";
    
    @Test
    public void testHtmlToTextConversion() throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(HTML_EXAMPLE.getBytes());
        BodyContentHandler contenthandler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        parser.parse(bais, contenthandler, metadata, new ParseContext());
        assertEquals("\nThis is a very simple HTML document\n" + 
            "\n" + 
            "It only has two paragraphs ö\n" + 
            "\n", contenthandler.toString().replace("\r", ""));
        assertEquals("A Simple HTML Document", metadata.get("title"));
        assertEquals("A Simple HTML Document", metadata.get("dc:title"));
        assertNull(metadata.get("title2"));
        assertEquals("org.apache.tika.parser.DefaultParser", metadata.getValues("X-Parsed-By")[0]);
        assertEquals("org.apache.tika.parser.html.HtmlParser", metadata.getValues("X-Parsed-By")[1]);
        assertEquals("ISO-8859-1", metadata.get("Content-Encoding"));
        assertEquals("text/html; charset=ISO-8859-1", metadata.get("Content-Type"));
        assertEquals("noindex,noarchive", metadata.get("robots"));
        assertEquals("abc,def", metadata.get("keywords"));
    }
}
