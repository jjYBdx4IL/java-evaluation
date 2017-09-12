package javascript;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.jjYBdx4IL.utils.cache.SimpleDiskCacheEntry;
import com.github.jjYBdx4IL.utils.cache.SimpleDiskCacheEntry.UpdateMode;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DOMPurifyTest extends AbstractHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DOMPurifyTest.class);

    private Server server = null;

    @Before
    public void before() throws Exception {
        server = new Server(0);
        server.setHandler(this);
        server.start();
    }

    @After
    public void after() throws Exception {
        server.stop();
    }

    @Ignore // not working
    @Test
    public void testPurify() throws Exception {

        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.setAlertHandler(new AlertHandler() {

                @Override
                public void handleAlert(Page page, String message) {
                    LOG.info(message);
                }
            });

            HtmlPage page = webClient.getPage(getUrl("/"));
            webClient.waitForBackgroundJavaScript(10000L);
            page = webClient.getPage(getUrl("/"));
            assertEquals("", page.getTitleText());

            final String pageAsXml = page.asXml();
            assertTrue(pageAsXml.contains("<script>"));

            final String pageAsText = page.asText();
        }

    }

    public URL getUrl(String path) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(String.format(Locale.ROOT, "%s://%s:%d%s", "http", addr.getHostAddress(),
            connector.getLocalPort(), path));
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

        if ("/".equals(target)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
            response.getWriter()
                .print(""
                    + "<html>\n"
                    + "<head></head>\n"
                    + "<body onload=\"myFunction()\">\n"
                    + "<script src=\"/script.js\"></script><script>\n"
                    + "function myFunction() {\n"
                    + "window.alert(DOMPurify.sanitize('a<iframe/>'));\n"
                    + "}\n"
                    + "</script></html>");
        } else if ("/script.js".equals(target)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/javascript");
            SimpleDiskCacheEntry sde = new SimpleDiskCacheEntry(
                "https://cdnjs.cloudflare.com/ajax/libs/dompurify/1.0.2/purify.min.js",
                UpdateMode.NEVER);
            try (InputStream is = sde.getInputStream()) {
                IOUtils.copy(is, response.getWriter(), "UTF-8");
            }
        }

        baseRequest.setHandled(true);
    }
}
