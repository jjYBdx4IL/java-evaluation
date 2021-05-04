package org.eclipse.jetty.websocket;

import java.util.concurrent.CountDownLatch;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ClientEventSocket extends WebSocketAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientEventSocket.class);
    private final CountDownLatch messageReceivedCountDownLatch;
    
    public ClientEventSocket(CountDownLatch messageReceivedCountDownLatch) {
        super();
        this.messageReceivedCountDownLatch = messageReceivedCountDownLatch;
    }
    
    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        LOG.info("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        LOG.info("Received TEXT message: >>>" + message + "<<<");
        messageReceivedCountDownLatch.countDown();
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        LOG.info("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        LOG.error("", cause);
    }

}
