package org.apache.http.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientTest.class);

    public static final String url = "https://www.google.de";

    @Test
    public void testGet() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());

        HttpGet g = new HttpGet(url);

        HttpHost target = new HttpHost(g.getURI().getHost(), g.getURI().getPort());

        // createSystem() allows use of proxy via
        // -Dhttp.proxyHost=xyz -Dhttp.proxyPort=3128
        CloseableHttpClient httpclient = HttpClients.createSystem();

        try (CloseableHttpResponse response = httpclient.execute(target, g)) {
            String reply = EntityUtils.toString(response.getEntity(), UTF_8);
            int status = response.getStatusLine().getStatusCode();
            LOG.debug("status = {}, reply = {}", status, reply);
        }
    }
}
