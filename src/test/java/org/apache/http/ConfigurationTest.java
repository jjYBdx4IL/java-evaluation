package org.apache.http;

import static org.junit.Assert.assertEquals;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see <a href="http://www.baeldung.com/httpclient4">http://www.baeldung.com/httpclient4</a>
 * @author Github jjYBdx4IL Projects
 */
public class ConfigurationTest extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationTest.class);
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
    
    @Test
    public void testSetClientReferer() throws IOException {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader(HttpHeaders.REFERER, "http://my.referer"));
        try (CloseableHttpClient httpclient = HttpClients.custom().
                setDefaultHeaders(headers).
                build()) {
            HttpGet httpGet = new HttpGet(getUrl("/200").toExternalForm());
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
            }
        }
    }

    @Test
    public void testSetClientTimeouts() throws IOException {
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(30000).
                setConnectionRequestTimeout(30000).
                setSocketTimeout(30000).
                setAuthenticationEnabled(false).
                setContentCompressionEnabled(true).
                setCircularRedirectsAllowed(false).
                build();

        try (CloseableHttpClient httpclient = HttpClients.custom().
                setDefaultRequestConfig(requestConfig).
                disableCookieManagement().
                build()) {
            HttpGet httpGet = new HttpGet(getUrl("/200").toExternalForm());
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
            }
        }
    }

    @Test
    public void testSetPerRequestTimeout() throws IOException {
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(30000).
                setConnectionRequestTimeout(30000).
                setSocketTimeout(30000).
                build();

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(getUrl("/200").toExternalForm());
            httpGet.setConfig(requestConfig);
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
                assertEquals("text/plain", response.getEntity().getContentType().getElements()[0].getName());
                assertEquals("charset", response.getEntity().getContentType().getElements()[0].getParameter(0).getName());
                assertEquals("iso-8859-1", response.getEntity().getContentType().getElements()[0].getParameter(0).getValue());
                assertEquals("status: 200", IOUtils.toString(response.getEntity().getContent()));
            }
        }
    }

    @Test
    public void testFollowRedirects() throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(getUrl("/301").toExternalForm());
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
            }
        }
    }

    @Test
    public void testNoFollowRedirects() throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.custom().disableRedirectHandling().build()) {
            HttpGet httpGet = new HttpGet(getUrl("/301").toExternalForm());
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                assertEquals(HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatusLine().getStatusCode());
            }
        }
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

        Enumeration<String> headerNames = request.getHeaderNames(); 
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders(name);
            while (headerValues.hasMoreElements()) {
                LOG.info("hdr rcvd by srvr: " + name + ": " + headerValues.nextElement());
            }
        }
        
        int status = Integer.parseInt(target.substring(1));
        response.setStatus(status);
        if (status == HttpServletResponse.SC_MOVED_PERMANENTLY) {
            response.setHeader("Location", "/200");
        } else {
            response.setContentType("text/plain");
            response.getWriter().print("status: " + status);
        }

        baseRequest.setHandled(true);
    }
     
}
