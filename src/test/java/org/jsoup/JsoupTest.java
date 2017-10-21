package org.jsoup;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class JsoupTest {

    private static final Logger LOG = LoggerFactory.getLogger(JsoupTest.class);

    @Ignore
    public void _testAbsRef(String tag, String refAttr) {
        Document doc = null;
        Element link;

        try {
            //doc = Jsoup.parse("<head><"+tag+" "+refAttr+"=\"2\"></"+tag+"></head>");
            doc = Jsoup.parse("<html><body><" + tag + " " + refAttr + "=\"2\"></" + tag + "></body></html>");
            doc.setBaseUri("http://www.test.de/some/page/1");
            link = doc.select(tag).first();
            assertEquals("2", link.attr(refAttr));
            assertEquals("http://www.test.de/some/page/2", link.attr("abs:" + refAttr));

            doc = Jsoup.parse("<html><head><" + tag + " " + refAttr + "=\"../2\"></" + tag + "></head></html>");
            doc.setBaseUri("http://www.test.de/some/page/1");
            link = doc.select(tag).first();
            assertEquals("../2", link.attr(refAttr));
            assertEquals("http://www.test.de/some/2", link.attr("abs:" + refAttr));

            doc = Jsoup.parse("<html><head><" + tag + " " + refAttr + "=\"/2\"></" + tag + "></head></html>");
            doc.setBaseUri("http://www.test.de/some/page/1");
            link = doc.select(tag).first();
            assertEquals("/2", link.attr(refAttr));
            assertEquals("http://www.test.de/2", link.attr("abs:" + refAttr));
        } catch (Exception ex) {
            LOG.info(ex.toString());
            LOG.info("tag: " + tag);
            LOG.info("refAttr: " + refAttr);
            if (doc != null) {
                LOG.info(doc.toString());
            }
            throw new Error(ex);
        }
    }

    @Ignore
    public void _testAbsRefFrameSet(String tag, String refAttr) {
        Document doc = null;
        Element link;

        try {
            doc = Jsoup.parse("<frameset><" + tag + " " + refAttr + "=\"2\"></" + tag + "></frameset>");
            doc.setBaseUri("http://www.test.de/some/page/1");
            link = doc.select(tag).first();
            assertEquals("2", link.attr(refAttr));
            assertEquals("http://www.test.de/some/page/2", link.attr("abs:" + refAttr));

            doc = Jsoup.parse("<html><frameset><" + tag + " " + refAttr + "=\"../2\"></" + tag + "></frameset></html>");
            doc.setBaseUri("http://www.test.de/some/page/1");
            link = doc.select(tag).first();
            assertEquals("../2", link.attr(refAttr));
            assertEquals("http://www.test.de/some/2", link.attr("abs:" + refAttr));

            doc = Jsoup.parse("<html><frameset><" + tag + " " + refAttr + "=\"/2\"></" + tag + "></frameset></html>");
            doc.setBaseUri("http://www.test.de/some/page/1");
            link = doc.select(tag).first();
            assertEquals("/2", link.attr(refAttr));
            assertEquals("http://www.test.de/2", link.attr("abs:" + refAttr));
        } catch (Exception ex) {
            LOG.info(ex.toString());
            LOG.info("tag: " + tag);
            LOG.info("refAttr: " + refAttr);
            if (doc != null) {
                LOG.info(doc.toString());
            }
            throw new Error(ex);
        }
    }

    @Test
    public void testJsoupAbsRef() throws IOException {
        _testAbsRef("a", "href");
        _testAbsRef("img", "src");
        _testAbsRef("link", "href");
        _testAbsRefFrameSet("frame", "src");
    }

    @Test
    public void testJsoup2() {
        Document doc = Jsoup.parse("<html><head>"
                + "  <liNk type=\"text/css\" rel=\"stylesheet\" hrEf=\"/rez/style.css\" />"
                + "  <scripT sRc=\"/rez/prettify.js\"></script>"
                + "</head><body>"
                + "  <script src=\"/rez/prettify2.js\"></script>"
                + "  <iMg Src=\"somepic.png\" alt=\"desc\" />"
                + "  <A Href=\"http://www.google.de\">google</a>"
                + "</body></html>");
        doc.setBaseUri("http://www.test.de");
        
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

        assertEquals(1, links.size());
        assertEquals("a", links.get(0).tagName());
        assertEquals("http://www.google.de", links.get(0).attr("href"));
        assertEquals("http://www.google.de", links.get(0).attr("abs:href"));
        
        assertEquals(3, media.size());
        assertEquals("script", media.get(0).tagName());
        assertEquals("/rez/prettify.js", media.get(0).attr("src"));
        assertEquals("http://www.test.de/rez/prettify.js", media.get(0).attr("abs:src"));
        assertEquals("script", media.get(1).tagName());
        assertEquals("/rez/prettify2.js", media.get(1).attr("src"));
        assertEquals("http://www.test.de/rez/prettify2.js", media.get(1).attr("abs:src"));
        assertEquals("img", media.get(2).tagName());
        assertEquals("somepic.png", media.get(2).attr("src"));
        assertEquals("http://www.test.de/somepic.png", media.get(2).attr("abs:src"));
        
        assertEquals(1, imports.size());
        assertEquals("link", imports.get(0).tagName());
        assertEquals("/rez/style.css", imports.get(0).attr("href"));
        assertEquals("http://www.test.de/rez/style.css", imports.get(0).attr("abs:href"));
    }
    
    @Test
    public void testText() {
        Document doc = Jsoup.parse("<html><head>"
            + "  <liNk type=\"text/css\" rel=\"stylesheet\" hrEf=\"/rez/style.css\" />"
            + "  <scripT sRc=\"/rez/prettify.js\"></script>"
            + "</head><body>"
            + "  <script src=\"/rez/prettify2.js\"></script>"
            + "  <iMg Src=\"somepic.png\" alt=\"desc\" />"
            + "  <A Href=\"http://www.google.de\">google</a>"
            + "<p>one</p><p>two</p><p>three<p>four"
            + "</body></html>");
        doc.setBaseUri("http://www.test.de");
        assertEquals("google one two three four", doc.text());
    }
}