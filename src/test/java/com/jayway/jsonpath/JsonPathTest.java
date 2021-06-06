package com.jayway.jsonpath;

import static org.junit.Assert.assertEquals;

import com.google.common.net.MediaType;

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

public class JsonPathTest extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JsonPathTest.class);

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
        response.getWriter().print("{\n" +
            "  \"responseHeader\":{\n" +
            "    \"status\":0,\n" +
            "    \"QTime\":7},\n" +
            "  \"overlay\":{\n" +
            "    \"znodeVersion\":0,\n" +
            "    \"userProps\":{\"update.autoCreateFields\":\"false\"}}}");

        baseRequest.setHandled(true);
    }

    @Test
    public void testJsonPath() throws Exception {
        try {
            JsonPath.parse(getUrl("")).read("$['overlay']['userProps']['update.autoCreateFields.2']");
        } catch (PathNotFoundException ex) {
        }
        assertEquals("false", 
            JsonPath.parse(getUrl("")).read("$['overlay']['userProps']['update.autoCreateFields']"));
        assertEquals(7, (int) JsonPath.parse(getUrl("")).read("$['responseHeader']['QTime']"));
    }

    @Test
    public void testJsonPath2() throws Exception {
        assertEquals(7, (int) JsonPath.parse(getUrl("")).read("$.responseHeader.QTime"));

        net.minidev.json.JSONArray ja = JsonPath.parse(getUrl("")).read("$.responseHeader[?(@.status)].QTime");
        assertEquals(1, ja.size());
        assertEquals(7, (int) ja.get(0));
    }
}
