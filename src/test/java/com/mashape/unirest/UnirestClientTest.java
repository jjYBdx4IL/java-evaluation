package com.mashape.unirest;

import static org.junit.Assert.assertEquals;

import com.google.common.net.MediaType;
import com.mashape.unirest.http.Unirest;
import org.apache.tika.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UnirestClientTest extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(UnirestClientTest.class);

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

    public URL getUrl(String path) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(
            String.format(Locale.ROOT, "%s://%s:%d%s", "http", addr.getHostAddress(), connector.getLocalPort(), path));
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.JSON_UTF_8.toString());
        if ("POST".equals(request.getMethod())) {
            String postData = IOUtils.toString(request.getReader());
            response.getWriter().print(postData);
        } else {
            response.getWriter().print("{\"a\":\"a\"}");
        }

        baseRequest.setHandled(true);
    }

    @Test
    public void testUnirestClient() throws Exception {
        String url = getUrl("").toExternalForm();
        assertEquals("{\"a\":\"a\"}", Unirest.get(url).asString().getBody());
        assertEquals("{\"a\":\"123\"}", Unirest.post(url).body("{\"a\":\"123\"}")
            .asString().getBody());
    }
}
