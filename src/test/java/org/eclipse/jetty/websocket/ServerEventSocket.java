package org.eclipse.jetty.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ServerEventSocket extends WebSocketAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ServerEventSocket.class);
    
    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        LOG.info("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        LOG.info("Received TEXT message: " + message);
        getRemote().sendString("Received TEXT message: >>>" + message + "<<<", new WriteCallback() {
            @Override
            public void writeFailed(Throwable x) {
                LOG.warn("write failed");
            }

            @Override
            public void writeSuccess() {
                LOG.info("write success");
            }
        });
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
