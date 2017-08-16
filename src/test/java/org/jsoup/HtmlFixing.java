package org.jsoup;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class HtmlFixing {

    private static final Logger logger = Logger.getLogger(HtmlFixing.class.getName());

    @Test
    public void testJsoupHtmlFixMissingPCloseTag() {
        String html = "<html><head><title>First parse</title></head>"
                + "<body><p>Parsed HTML into<p>a doc.</p></body></html>";
        Document doc = Jsoup.parse(html);
        logger.debug(doc.toString());
        assertTrue(doc.toString().indexOf("into</p>") > -1);
    }

    @Test
    public void testJsoupHtmlFixMissingTBodyTags() {
        String html = "<html><head><title>First parse</title></head>"
                + "<body><table><tr><td>1</td></tr></table></body></html>";
        Document doc = Jsoup.parse(html);
        logger.debug(doc.toString());
        assertTrue(doc.toString().indexOf("<tbody>") > -1);
        assertTrue(doc.toString().indexOf("</tbody>") > -1);
    }
}
