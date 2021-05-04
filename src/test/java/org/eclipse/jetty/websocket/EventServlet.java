package org.eclipse.jetty.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 *
 * @author jjYBdx4IL
 */
@SuppressWarnings(value = "serial")
public class EventServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(ServerEventSocket.class);
    }

}
