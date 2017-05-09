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
    private final LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    // poison object:
    private final Message shutdownMessage = new Message(null);

    public ChatServer() {
    }

    public synchronized void add(Session session) {
        sessions.add(session);
        LOG.info("number of active sessions: " + sessions.size());
    }

    public synchronized void received(Message message) {
        if (message == null) {
            throw new NullPointerException();
        }
        messages.add(message);
        LOG.info("messages in queue: " + messages.size());
    }

    @Override
    public void run() {
        LOG.info("chat server thread started");
        while (true) {
            LOG.info("server loop start");
            Message message = null;
            try {
                message = messages.take();
            } catch (InterruptedException ex) {
                LOG.error("", ex);
                continue;
            }
            if (message == shutdownMessage) {
                break;
            }
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
        }
        LOG.info("chat server thread stopped");
        shutdownLatch.countDown();
    }

    public synchronized void remove(Session session) {
        sessions.remove(session);
        LOG.info("connections total: " + sessions.size());
    }

    public synchronized void shutdown() {
        LOG.info("signaling shutdown to chat server thread");
        messages.add(shutdownMessage);
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
