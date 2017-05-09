package org.eclipse.jetty.websocket.chat;

import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ChatHandler extends WebSocketHandler implements LifeCycle.Listener, Container.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(ChatHandler.class);
    
    private final ChatServer chatServer = new ChatServer();
    
    public ChatHandler() {
    }
    
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator(new ChatCreator(chatServer));
        addLifeCycleListener(this);
        addEventListener(this);
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        LOG.info("lifeCycleStarting");
    }

    @Override
    public void lifeCycleStarted(LifeCycle event) {
        LOG.info("lifeCycleStarted");
        Thread thread = new Thread(chatServer);
        thread.start();
    }

    @Override
    public void lifeCycleFailure(LifeCycle event, Throwable cause) {
        LOG.info("lifeCycleFailure");
    }

    @Override
    public void lifeCycleStopping(LifeCycle event) {
        LOG.info("lifeCycleStopping");
        chatServer.shutdown();
    }

    @Override
    public void lifeCycleStopped(LifeCycle event) {
        LOG.info("lifeCycleStopped");
    }

    @Override
    public void beanAdded(Container parent, Object child) {
        LOG.info("beanAdded");
    }

    @Override
    public void beanRemoved(Container parent, Object child) {
        LOG.info("beanRemoved");
    }

}
