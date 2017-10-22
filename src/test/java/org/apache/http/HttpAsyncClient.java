package org.apache.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;
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
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpAsyncClient extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HttpAsyncClient.class);

    CloseableHttpAsyncClient httpclient;

    @Before
    public void before() throws Exception {
        server = new Server(0);
        server.setHandler(this);
        server.start();

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader(HttpHeaders.REFERER, "http://my.referer"));
        RequestConfig requestConfig = RequestConfig
            .custom()
            .setConnectTimeout(30000)
            .setConnectionRequestTimeout(30000)
            .setSocketTimeout(30000)
            .build();
        httpclient = HttpAsyncClients
            .custom()
            .setDefaultHeaders(headers)
            .setDefaultRequestConfig(requestConfig)
            .build();
        httpclient.start();
    }

    @After
    public void after() throws Exception {
        server.stop();
    }

    @Test
    public void test() throws InterruptedException, ExecutionException, IOException {

        // Execute request
        final HttpGet request1 = new HttpGet(getUrl("/one").toExternalForm());
        Future<HttpResponse> future = httpclient.execute(request1, null);
        // and wait until a response is received
        HttpResponse response1 = future.get();
        LOG.info(request1.getRequestLine() + "->" + response1.getStatusLine());
        LOG.info(IOUtils.toString(response1.getEntity().getContent(), "UTF-8"));

        // One most likely would want to use a callback for operation result
        final CountDownLatch latch1 = new CountDownLatch(1);
        final HttpGet request2 = new HttpGet(getUrl("/two").toExternalForm());
        httpclient.execute(request2, new FutureCallback<HttpResponse>() {
            public void completed(final HttpResponse response2) {
                latch1.countDown();
                LOG.info(request2.getRequestLine() + "->" + response2.getStatusLine());
                try {
                    LOG.info(IOUtils.toString(response2.getEntity().getContent(), "UTF-8"));
                } catch (UnsupportedOperationException | IOException ex) {
                    LOG.info("", ex);
                }
            }

            public void failed(final Exception ex) {
                latch1.countDown();
                LOG.info(request2.getRequestLine() + "->" + ex);
            }

            public void cancelled() {
                latch1.countDown();
                LOG.info(request2.getRequestLine() + " cancelled");
            }
        });
        latch1.await();

        // In real world one most likely would also want to stream
        // request and response body content
        final CountDownLatch latch2 = new CountDownLatch(1);
        final HttpGet request3 = new HttpGet(getUrl("/three").toExternalForm());
        HttpAsyncRequestProducer producer3 = HttpAsyncMethods.create(request3);
        AsyncCharConsumer<HttpResponse> consumer3 = new AsyncCharConsumer<HttpResponse>() {

            HttpResponse response;

            @Override
            protected void onResponseReceived(final HttpResponse response) {
                this.response = response;
            }

            @Override
            protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
                // Do something useful
            }

            @Override
            protected void releaseResources() {
            }

            @Override
            protected HttpResponse buildResult(final HttpContext context) {
                return this.response;
            }

        };
        httpclient.execute(producer3, consumer3, new FutureCallback<HttpResponse>() {

            public void completed(final HttpResponse response3) {
                latch2.countDown();
                LOG.info(request3.getRequestLine() + "->" + response3.getStatusLine());
                try {
                    LOG.info(IOUtils.toString(response3.getEntity().getContent(), "UTF-8"));
                } catch (UnsupportedOperationException | IOException ex) {
                    LOG.info("", ex);
                }
            }

            public void failed(final Exception ex) {
                latch2.countDown();
                LOG.info(request3.getRequestLine() + "->" + ex);
            }

            public void cancelled() {
                latch2.countDown();
                LOG.info(request3.getRequestLine() + " cancelled");
            }

        });
        latch2.await();
    }

    private Server server = null;

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
        response.setContentType("text/plain");
        response.getWriter().print("target:" + target);

        baseRequest.setHandled(true);
    }

}
