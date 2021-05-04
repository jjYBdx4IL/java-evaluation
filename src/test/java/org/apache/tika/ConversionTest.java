package org.apache.tika;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
        InputStream bais = new ByteArrayInputStream(HTML_EXAMPLE.getBytes());
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

    @Test
    public void testRtfToTextConversion() throws IOException, SAXException, TikaException {
        try (InputStream bais = new FileInputStream("src/test/resources/message.rtf")) {
            BodyContentHandler contenthandler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            AutoDetectParser parser = new AutoDetectParser();
            parser.parse(bais, contenthandler, metadata, new ParseContext());
            assertEquals("Here are the stats from the Caffee guy, nadda. \n" + 
                "\n" + 
                "Solomon Foshko\n" + 
                "Strategic Forecasting, Inc.\n" + 
                "Stratfor Customer Service\n" + 
                "T: 512.744.4089\n" + 
                "F: 512.744.4334\n" + 
                "Solomon.Foshko@stratfor.com\n" + 
                "www.stratfor.com\n" + 
                "\n" + 
                "\n" + 
                "Get Free Time on Your Subscription with Stratfor's New Referral Rewards\n" + 
                "Program!  Ask me how you can have extra days, months or years added to your\n" + 
                "subscription with Stratfor's new Referral Rewards Program!  Or find out at\n" + 
                "www.stratfor.com/referral. \n" + 
                "\n" + 
                "\n" + 
                "-----Original Message-----\n" + 
                "From: Michael Mooney [mailto:mooney@stratfor.com] \n" + 
                "Sent: Thursday, March 22, 2007 10:45 AM\n" + 
                "To: Solomon Foshko\n" + 
                "Subject: Re: FW: unsubscribe--e-mail\n" + 
                "\n" + 
                "nothing there, can't even find it in the database as an account with no\n" + 
                "subscriptions.\n" + 
                "\n" + 
                "Solomon Foshko wrote:\n" + 
                ">\n" + 
                "> Hey Mike can you pull the stats on this subscriber...\n" + 
                "> rhcbonitabay@webtv.net <mailto:rhcbonitabay@webtv.net>\n" + 
                ">\n" + 
                ">  \n" + 
                ">\n" + 
                "> Thanks,\n" + 
                ">\n" + 
                ">  \n" + 
                ">\n" + 
                "> Solomon Foshko\n" + 
                "> **Strategic Forecasting, Inc.***\n" + 
                "> **Stratfor Customer Service***\n" + 
                "> T: 512.744.4089\n" + 
                "> F: 512.744.4334\n" + 
                "> Solomon.Foshko@stratfor.com <mailto:Solomon.Foshko@stratfor.com>\n" + 
                "> www.stratfor.com <http://www.stratfor.com/>\n" + 
                ">\n" + 
                ">\n" + 
                "> Get Free Time on Your Subscription with Stratfor's New Referral Rewards\n" + 
                "> Program!  Ask me how you can have extra days, months or years added to\n" + 
                "> your\n" + 
                "> subscription with Stratfor's new Referral Rewards Program!  Or find out at\n" + 
                "> www.stratfor.com/referral <http://www.stratfor.com/referral>.\n" + 
                ">\n" + 
                "> -----Original Message-----\n" + 
                "> *From:* Stratfor Customer Service [mailto:service@stratfor.com]\n" + 
                "> *Sent:* Tuesday, March 20, 2007 3:28 PM\n" + 
                "> *To:* Hunter Caffee\n" + 
                "> *Subject:* Re: unsubscribe--e-mail\n" + 
                ">\n" + 
                ">  \n" + 
                ">\n" + 
                "> Dear Mr. Caffee,\n" + 
                ">\n" + 
                "> I have asked our IT Dept to pull your name from our distribution list\n" + 
                "> as well as terminated your account with Stratfor. This is the first\n" + 
                "> time I have seen your email and I apologize that you have attempted\n" + 
                "> many times to unsubscribe and have been unsuccessful. If possible can\n" + 
                "> you forward me the previous emails you have sent as an attachment so\n" + 
                "> that I can trace the mail headers.\n" + 
                ">\n" + 
                "> Thank you very much,\n" + 
                ">\n" + 
                "> Solomon Foshko\n" + 
                "> *Strategic Forecasting, Inc.\n" + 
                "> *Stratfor Customer Service\n" + 
                "> T: 512.744.4089\n" + 
                "> F: 512.744.4334\n" + 
                "> Solomon.Foshko@stratfor.com\n" + 
                "> www.stratfor.com\n" + 
                ">\n" + 
                ">  \n" + 
                "> *Get Free Time on Your Subscription with Stratfor's New Referral Rewards\n" + 
                "> Program! *Ask me how you can have extra days, months or years added to\n" + 
                "> your\n" + 
                "> subscription with Stratfor's new Referral Rewards Program!  Or find out at\n" + 
                "> www.stratfor.com/referral.\n" + 
                ">\n" + 
                "> ------------------------------------------------------------------------\n" + 
                ">\n" + 
                "> *From: *Hunter Caffee <rhcbonitabay@webtv.net>\n" + 
                "> *Date: *Tue, 20 Mar 2007 20:07:54 GMT\n" + 
                "> *To: *<service@stratfor.com>\n" + 
                "> *Subject: *unsubscribe--e-mail\n" + 
                ">\n" + 
                "> rhcbonitabay@webtv.net. This is the seventh time I have attempted to\n" + 
                "> unsubscribe. If you do not unsubscribe me this time I will file a\n" + 
                "> complaint against you with the FCC. If that doesn't work I am going to\n" + 
                "> file a harrassment suit against you.  HunterCaffee           \n" + 
                ">                                   \n" + 
                ">\n" + 
                "\n" + 
                "", contenthandler.toString().replace("\r", ""));
            assertEquals(null, metadata.get("title"));
            assertEquals(null, metadata.get("dc:title"));
            assertNull(metadata.get("title2"));
            assertEquals("org.apache.tika.parser.DefaultParser", metadata.getValues("X-Parsed-By")[0]);
            assertEquals("org.apache.tika.parser.rtf.RTFParser", metadata.getValues("X-Parsed-By")[1]);
            assertEquals(null, metadata.get("Content-Encoding"));
            assertEquals("application/rtf", metadata.get("Content-Type"));
            assertEquals(null, metadata.get("robots"));
            assertEquals(null, metadata.get("keywords"));
        }
    }
}
