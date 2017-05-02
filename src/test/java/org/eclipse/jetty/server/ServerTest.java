package org.eclipse.jetty.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.handler.AbstractHandler;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerTest extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ServerTest.class);

    public URL getURL(Server server) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(
                String.format(Locale.ROOT, "%s://%s:%d", "http", addr.getHostAddress(), connector.getLocalPort()));
    }

    @Test
    public void testAutoSetup() throws Exception {
        Server server = new Server(0);
        server.setHandler(this);
        server.start();
        URL serverURL = getURL(server);
        LOG.info("server URL: " + serverURL);
        String pageContents = IOUtils.toString(serverURL, "ASCII");
        LOG.info("test page contents: " + pageContents);
        assertEquals("some test page content", pageContents);
        server.stop();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain");
        response.getWriter().print("some test page content");

        baseRequest.setHandled(true);
    }
}
