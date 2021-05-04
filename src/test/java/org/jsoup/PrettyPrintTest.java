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
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author jjYBdx4IL
 */
public class PrettyPrintTest {

    @Test
    public void test1() {
        String html = "<html><head><title>First parse</title></head>"
                + "<body><p>Parsed HTML into a doc.</p></body></html>";

        Document doc = Jsoup.parse(html);
        doc.outputSettings().prettyPrint(true);
        doc.outputSettings().indentAmount(4);
        //System.out.println(doc);
        assertEquals("<html>\n"
                + "    <head>\n"
                + "        <title>First parse</title>\n"
                + "    </head>\n"
                + "    <body>\n"
                + "        <p>Parsed HTML into a doc.</p>\n"
                + "    </body>\n"
                + "</html>", doc.toString());
    }
}
