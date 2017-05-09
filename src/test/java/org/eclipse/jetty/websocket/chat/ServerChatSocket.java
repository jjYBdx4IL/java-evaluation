package org.eclipse.jetty.websocket.chat;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ServerChatSocket extends WebSocketAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ServerChatSocket.class);

    private final ChatServer chatServer;

    public ServerChatSocket(ChatServer chatServer) {
        LOG.info("ServerChatSocket instance created with chat server instance " + chatServer);
        this.chatServer = chatServer;
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        chatServer.add(sess);
        LOG.info("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        LOG.info("Received TEXT message: " + message);
        chatServer.received(message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        LOG.info("Socket Closed: [" + statusCode + "] " + reason);
        chatServer.remove(getSession());
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        LOG.error("", cause);
        chatServer.remove(getSession());
    }

}
