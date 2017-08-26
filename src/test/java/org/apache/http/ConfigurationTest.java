package org.apache.http;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.AdHocHttpServer;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @see <a href="http://www.baeldung.com/httpclient4">http://www.baeldung.com/httpclient4</a>
 * @author Github jjYBdx4IL Projects
 */
public class ConfigurationTest {

    private static AdHocHttpServer server;
    private static URL url200;
    private static URL url301;
    @SuppressWarnings("unused")
	private static URL url404;
    @SuppressWarnings("unused")
	private static URL url500;

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = new AdHocHttpServer();
        url200 = server.addStaticContent("/sc200",
                new AdHocHttpServer.StaticResponse("A", HttpServletResponse.SC_OK));
        url301 = server.addStaticContent("/sc301",
                new AdHocHttpServer.StaticResponse(url200.toExternalForm(), HttpServletResponse.SC_MOVED_PERMANENTLY));
        url404 = server.addStaticContent("/sc404",
                new AdHocHttpServer.StaticResponse("B", HttpServletResponse.SC_NOT_FOUND));
        url500 = server.addStaticContent("/sc500",
                new AdHocHttpServer.StaticResponse("C", HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testSetClientTimeouts() throws IOException {
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(30000).
                setConnectionRequestTimeout(30000).
                setSocketTimeout(30000).
                build();

        try (CloseableHttpClient httpclient = HttpClients.custom().
                setDefaultRequestConfig(requestConfig).
                build()) {
            HttpGet httpGet = new HttpGet(url200.toExternalForm());
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
            HttpGet httpGet = new HttpGet(url200.toExternalForm());
            httpGet.setConfig(requestConfig);
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
            }
        }
    }

    @Test
    public void testFollowRedirects() throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url301.toExternalForm());
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
            }
        }
    }

    @Test
    public void testNoFollowRedirects() throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.custom().disableRedirectHandling().build()) {
            HttpGet httpGet = new HttpGet(url301.toExternalForm());
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                assertEquals(HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatusLine().getStatusCode());
            }
        }
    }
}
