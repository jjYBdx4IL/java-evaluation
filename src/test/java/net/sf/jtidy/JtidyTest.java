package net.sf.jtidy;

import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.tidy.Tidy;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

public class JtidyTest {

    @Test
    public void test() {
        String input = "<html><body><main>a</main></html>";
        assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n"
            + "<html>\n"
            + "  <head>\n"
            + "    <title>\n"
            + "    </title>\n"
            + "  </head>\n"
            + "  <body>\n"
            + "    <main>\n"
            + "      a\n"
            + "    </main>\n"
            + "  </body>\n"
            + "</html>\n", tidyHtml(input));
    }

    public static String tidyHtml(String inputHtml) {
        Properties oProps = new Properties();
        oProps.setProperty("new-blocklevel-tags", "main");

        Tidy tidy = new Tidy();
        tidy.setConfigurationFromProps(oProps);
        tidy.setXHTML(false);
        tidy.setDocType("loose");
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);
        tidy.setTidyMark(false);
        tidy.setIndentContent(true);
        StringWriter writer = new StringWriter();
        tidy.parse(new StringReader(inputHtml), writer);
        return writer.toString().replace("\r", "");
    }
}
