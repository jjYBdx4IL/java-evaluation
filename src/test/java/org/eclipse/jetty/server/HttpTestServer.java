package org.eclipse.jetty.server;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.io.IOUtils.write;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Ignore;

/**
 * A server for answering HTTP requests with test response data.
 */
@Ignore
public final class HttpTestServer {

    public static final int HTTP_PORT = 50036;
    private Server _server;
    private String _responseBody;
    private String _requestBody;
    private String _mockResponseData;

    public HttpTestServer() {
    }

    public HttpTestServer(String mockData) {
        setMockResponseData(mockData);
    }

    public void start() throws Exception {
        configureServer();
        startServer();
    }

    private void startServer() throws Exception {
        _server.start();
    }

    protected void configureServer() {
        _server = new Server(HTTP_PORT);
        _server.setHandler(getMockHandler());
    }

    public Handler getMockHandler() {
        Handler handler = new AbstractHandler() {

            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                setResponseBody(getMockResponseData());
                setRequestBody(IOUtils.toString(baseRequest.getInputStream(), "UTF-8"));
                response.setStatus(SC_OK);
                response.setContentType("text/xml;charset=utf-8");
                write(getResponseBody(), response.getOutputStream(), "UTF-8");
                baseRequest.setHandled(true);
            }
        };
        return handler;
    }

    public void stop() throws Exception {
        _server.stop();
    }

    public void setResponseBody(String responseBody) {
        _responseBody = responseBody;
    }

    public String getResponseBody() {
        return _responseBody;
    }

    public void setRequestBody(String requestBody) {
        _requestBody = requestBody;
    }

    public String getRequestBody() {
        return _requestBody;
    }

    public static void main(String[] args) {
        HttpTestServer server = new HttpTestServer();
        try {
            server.start();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setMockResponseData(String mockResponseData) {
        _mockResponseData = mockResponseData;
    }

    public String getMockResponseData() {
        return _mockResponseData;
    }

    protected Server getServer() {
        return _server;
    }
}