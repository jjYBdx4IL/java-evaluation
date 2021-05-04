package org.eclipse.jetty.websocket.chat;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;

/**
 *
 * @author jjYBdx4IL
 */
public class ExtendedJettyConfigWebSocketChatTest extends WebSocketChatTestBase {

    @Override
    protected void configureServer() {
        
        ContextHandler chatContextHandler = new ContextHandler(CTX_PATH);
        chatContextHandler.setHandler(chatHandler);
        
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{contexts, new DefaultHandler()});
        
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[]{chatContextHandler, handlers});
        
        server.setHandler(handlerList);        
        
    }
    
}
