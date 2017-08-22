package com.j2html;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.jtidy.JtidyTest;

import static j2html.TagCreator.*;

// https://j2html.com/examples.html
public class HtmlGenTest {

    @Test
    public void test1() {
        String s = document(
            html(
                head(
                    title("Title")))).toString();
        assertEquals("<!DOCTYPE html><html><head><title>Title</title></head></html>", s);
    }

    @Test
    public void test2() {
        String s = document(
            html(
                head(
                    title("Title"),
                    link().withRel("stylesheet").withHref("/css/main.css")),
                body(
                    main(attrs("#main.content"),
                        h1("Heading!"))))).toString();
        assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n"
            + "<html>\n"
            + "  <head>\n"
            + "    <title>\n"
            + "      Title\n"
            + "    </title>\n"
            + "    <link type=\"text/css\" rel=\"stylesheet\" href=\"/css/main.css\">\n"
            + "  </head>\n"
            + "  <body>\n"
            + "    <main id=\"main\" class=\"content\">\n"
            + "      <h1>\n"
            + "        Heading!\n"
            + "      </h1>\n"
            + "    </main>\n"
            + "  </body>\n"
            + "</html>\n", JtidyTest.tidyHtml(s));
    }
}
