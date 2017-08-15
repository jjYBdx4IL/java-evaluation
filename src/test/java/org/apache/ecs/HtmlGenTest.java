package org.apache.ecs;

import static org.junit.Assert.*;

import org.apache.ecs.html.Option;
import org.junit.Test;

public class HtmlGenTest {

    @Test
    public void testHtmlGeneration() {
        Option optionElement = new Option();
        optionElement.setTagText("bar");
        optionElement.setValue("foo");
        optionElement.setSelected(false);

        assertEquals("<option value='foo'>bar</option>", optionElement.toString());

        Document doc = new Document();
        doc.appendBody(optionElement);

        assertEquals("<html><head><title></title></head><body><option value='foo'>bar</option></body></html>",
                doc.toString());
    }
}
