package org.eclipse.jetty.server;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.fit.cssbox.CssBoxTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;

//@meta:keywords:directory listing,file server@
/**
 * Jetty provides an easy way to set up a simple file server including directory
 * listing. This could be used with http://livejs.com/ for live-editing of local
 * html.
 *
 * @author jjYBdx4IL
 */
public class ResourceHandlerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceHandlerTest.class);

    private static Server server = null;

    @BeforeClass
    public static void before() throws Exception {
        server = new Server(0);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(CssBoxTest.getLocalExampleHomepageRoot().getAbsolutePath());
        server.setHandler(resourceHandler);
        server.start();
    }

    @AfterClass
    public static void after() throws Exception {
        server.stop();
    }

    public URL getUrl(String path) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(
            String.format(Locale.ROOT, "%s://%s:%d%s", "http", addr.getHostAddress(), connector.getLocalPort(), path));
    }

    @Test
    public void testAutoSetup() throws Exception {
        URL serverUrl = getUrl("/global.css");
        LOG.info("server URL: " + serverUrl);
        String pageContents = IOUtils.toString(serverUrl, "ASCII");
        LOG.info("test page contents: " + pageContents);
        assertTrue(pageContents, pageContents.contains("background-color: #fafafa;"));
    }

    @Test
    public void testAutoDirectoryListing() throws Exception {
        URL serverUrl = getUrl("/");
        LOG.info("server URL: " + serverUrl);
        String pageContents = IOUtils.toString(serverUrl, "ASCII");
        LOG.info("test page contents: " + pageContents);
        assertTrue(pageContents, pageContents.contains("cssbox_homepage.html"));
    }
}
