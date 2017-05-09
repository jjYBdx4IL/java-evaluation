package org.eclipse.jetty.websocket.chat;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatServer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ChatServer.class);

    private final Collection<Session> sessions = new HashSet<>();
    private final LinkedBlockingQueue<Message> userqueue = new LinkedBlockingQueue<>();
    // high priority queue for internal messages, so operational stuff isn't delayed by high
    // user load
    private final LinkedBlockingQueue<Message> highprio = new LinkedBlockingQueue<>();
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    public ChatServer() {
    }

    public void add(Message message) {
        if (message == null) {
            throw new NullPointerException();
        }
        userqueue.add(message);
        LOG.info("messages in queue: " + userqueue.size());
    }

    @Override
    public void run() {
        LOG.info("chat server thread started");
        boolean shutdown = false;
        while (!shutdown) {
            LOG.info("server loop start");
            Message message = highprio.poll();
            if (message == null) {
                try {
                    message = userqueue.take();
                } catch (InterruptedException ex) {
                    LOG.error("", ex);
                    continue;
                }
            }
            switch (message.getMessageType()) {
                case SHUTDOWN:
                    shutdown = true;
                    continue;
                case MSG:
                    for (Session sess : sessions) {
                        sess.getRemote().sendString("Received TEXT message: >>>" + message.getMessage() + "<<<", new WriteCallback() {
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
                    break;
                case CONNECT:
                    sessions.add(message.getSession());
                    LOG.info("connections total: " + sessions.size());
                    break;
                case DISCONNECT:
                    sessions.remove(message.getSession());
                    LOG.info("connections total: " + sessions.size());
                    break;
            }
        }
        LOG.info("chat server thread stopped");
        shutdownLatch.countDown();
    }

    public void shutdown() {
        LOG.info("signaling shutdown to chat server thread");
        highprio.add(Message.createShutdown());
        // make sure the server gets the shutdown message if it is listening to
        // the user queue:
        userqueue.add(Message.createShutdown());
    }

    public void wait4Shutdown(long timeoutSecs) {
        LOG.info("wait4Shutdown started");
        try {
            if (!shutdownLatch.await(timeoutSecs, TimeUnit.SECONDS)) {
                throw new RuntimeException();
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        LOG.info("wait4Shutdown done");
    }
}
