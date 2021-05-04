package org.eclipse.jetty.websocket.chat;

/**
 *
 * @author jjYBdx4IL
 */
public class WebSocketChatTest extends WebSocketChatTestBase {

    @Override
    protected void configureServer() {
        server.setHandler(chatHandler);
    }

}
