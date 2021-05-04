package org.jdom2;

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
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class XMLWriterTest {

    @Test
    public void testStringContentEscape() throws Exception {
        Element root = new Element("root");
        root.addContent("<");
        Document doc = new Document(root);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new XMLOutputter().output(doc, baos);
        System.out.println(baos.toString());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<root>&lt;</root>\r\n", baos.toString());

    }

    @Test(expected = IllegalDataException.class)
    public void testCDATA() throws Exception {
        new CDATA("]]>");
    }

    @Test
    public void testDocPrettyPrint() throws Exception {
        Element root = new Element("root");
        Element child = new Element("child");
        root.addContent(child);
        child.setAttribute("one", "1");
        child.addContent("some text");
        Document doc = new Document(root);

        Format f = Format.getPrettyFormat();
        f.setLineSeparator(LineSeparator.NL);
        f.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new XMLOutputter(f).output(doc, baos);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<root>\n"
                + "  <child one=\"1\">some text</child>\n"
                + "</root>\n", baos.toString());

    }

    @Test
    public void testPrettyPrintConvert() throws Exception {
        String unformattedXml
                = "<?xml version=\"1.0\" encoding=\"UTF-16\"?><QueryMessage\n"
                + "        xmlns=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message\"\n"
                + "        xmlns:query=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query\">\n"
                + "    <Query>\n"
                + "        <query:CategorySchemeWhere>\n"
                + "   \t\t\t\t\t         <query:AgencyID>ECB \n </query:AgencyID>\n"
                + "        </query:CategorySchemeWhere>\n"
                + "    </Query>\n\n\n\n\n"
                + "</QueryMessage>";
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(unformattedXml.getBytes("UTF-16")));
        Format f = Format.getPrettyFormat();
        f.setLineSeparator(LineSeparator.NL);
        f.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new XMLOutputter(f).output(doc, baos);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<QueryMessage xmlns=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message\" xmlns:query=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query\">\n"
                + "  <Query>\n"
                + "    <query:CategorySchemeWhere>\n"
                + "      <query:AgencyID>ECB \n"
                + " </query:AgencyID>\n"
                + "    </query:CategorySchemeWhere>\n"
                + "  </Query>\n"
                + "</QueryMessage>\n", baos.toString());
    }
}
