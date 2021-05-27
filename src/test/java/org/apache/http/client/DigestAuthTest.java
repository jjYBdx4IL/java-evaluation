package org.apache.http.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigestAuthTest {

    private static final Logger LOG = LoggerFactory.getLogger(DigestAuthTest.class);

    // wildfly test instance
    String loc = "http://localhost:9991/management";
    String user = "admin";
    String pass = "admin";
    
    @Ignore
    @Test
    public void testDigestAuthGet() throws Exception {
        URL url = new URL(loc);

        HttpHost target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
        HttpGet httpget = new HttpGet(url.toExternalForm());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pass);
        credsProvider.setCredentials(new AuthScope(target), credentials);
        CookieStore cookieStore = new BasicCookieStore();

        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore)
                .setDefaultCredentialsProvider(credsProvider).build();
                CloseableHttpResponse response = httpclient.execute(target, httpget);) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            LOG.info(IOUtils.toString(response.getEntity().getContent(), UTF_8));
        }
    }
    
    @Ignore
    @Test
    public void testDigestAuthPost() throws Exception {
        URL url = new URL(loc);

        HttpHost target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
        HttpPost post = new HttpPost(url.toExternalForm());
        // add, remove, read-resource
        post.setEntity(new StringEntity("{\"operation\" : \"read-resource\", \"address\" : {\"system-property\" : \"foo2\"}, \"content\" : {\"value\" : \"bar\"}, \"json.pretty\": 1}", ContentType.APPLICATION_JSON));
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pass);
        credsProvider.setCredentials(new AuthScope(target), credentials);
        CookieStore cookieStore = new BasicCookieStore();

        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore)
                .setDefaultCredentialsProvider(credsProvider).build();
                CloseableHttpResponse response = httpclient.execute(target, post);) {
            LOG.info(response.getStatusLine().getReasonPhrase());
            LOG.info(IOUtils.toString(response.getEntity().getContent(), UTF_8));
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }
    
    public static void main(String[] args) throws Exception {
        new DigestAuthTest().testDigestAuthGet();
        new DigestAuthTest().testDigestAuthPost();
    }
}
