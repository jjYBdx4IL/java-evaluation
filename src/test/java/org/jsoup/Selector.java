package org.jsoup;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class Selector {

    @Test
    public void testSelectChildAtTopOnly() {
        String html = "<html><head><title>First parse</title></head>"
                + "<body><table><tr><td>outer1"
                + "<table><tr><td>inner</td></tr></table>"
                + "</td><td>outer2</td></tr></table></body></html>";
        Document doc = Jsoup.parse(html);

        Element tr = doc.select("body > table > tbody > tr").get(0);
        // select only the outer tds:
        Elements tds = tr.select(":root > td");
        assertEquals(2, tds.size());
        assertEquals("outer1", tds.get(0).ownText());
        assertTrue(tds.get(0).html().indexOf("inner") > -1);
        assertEquals("outer2", tds.get(1).ownText());
    }
}
