package com.gargoylesoftware.htmlunit;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.jjYBdx4IL.test.AdHocHttpServer;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class HtmlUnitTest {

    public static final String JQUERY_WEBJAR_LOC = "/META-INF/resources/webjars/jquery/3.2.0/jquery.js";

    private static final Logger LOG = LoggerFactory.getLogger(HtmlUnitTest.class);
    private static AdHocHttpServer server;
    private static URL pageOne;
    private static URL pageTwo;
    private static URL pageJQuery;

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = new AdHocHttpServer();
        pageOne = server.addStaticContent("/one", new AdHocHttpServer.StaticResponse(
                "<html><title>title</title></html>"));
        pageTwo = server.addStaticContent("/two", new AdHocHttpServer.StaticResponse(
                "<html>\n"
                + "<head><script src=\"/jquery.js\"></script></head>\n"
                + "<body onload=\"myFunction()\">\n"
                + "<p id=\"myText\">inserted by js: </p>\n"
                + "<script>\n"
                + "function myFunction() {\n"
                + "    $( \"#myText\" ).html( $( \"#myText\" ).html() + \"Next Step...\" );\n"
                + "}\n"
                + "</script></html>"));

        // add jquery.js
        try (InputStream is = HtmlUnitTest.class.getResourceAsStream(JQUERY_WEBJAR_LOC)) {
            String js = IOUtils.toString(is, Charset.forName("UTF-8"));
            pageJQuery = server.addStaticContent("/jquery.js", new AdHocHttpServer.StaticResponse(
                    "application/javascript",
                    js
            ));
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (server != null) {
            server.close();
            server = null;
        }
    }

    @Test
    public void test() throws Exception {
        LOG.info(pageOne.toExternalForm());

        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(pageOne);
            Assert.assertEquals("title", page.getTitleText());

            final String pageAsXml = page.asXml();
            Assert.assertTrue(pageAsXml.contains("<title>"));

            final String pageAsText = page.asText();
            Assert.assertTrue(pageAsText.contains(""));
        }
    }
    
    @Test
    public void testWithJavascript() throws Exception {
        LOG.info(pageTwo.toExternalForm());

        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            final HtmlPage page = webClient.getPage(pageTwo);
            Assert.assertEquals("", page.getTitleText());

            final String pageAsXml = page.asXml();
            Assert.assertTrue(pageAsXml.contains("<script>"));

            final String pageAsText = page.asText();
            Assert.assertEquals("inserted by js: Next Step...", pageAsText);
        }
    }
}
