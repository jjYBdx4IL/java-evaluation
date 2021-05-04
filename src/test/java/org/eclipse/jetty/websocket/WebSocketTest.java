package org.eclipse.jetty.websocket;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class WebSocketTest {

    private Server server = null;
    private WebSocketClient client = null;

    @After
    public void after() throws Exception {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.stop();
        }
    }

    @Test
    public void testWebSocket() throws Exception {
        server = new Server(0);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/events/*");

        server.start();
        server.dump(System.err);

        URI uri = getURI(server);

        CountDownLatch wait4Reply = new CountDownLatch(1);
        
        client = new WebSocketClient();
        client.start();
        // The socket that receives events
        ClientEventSocket socket = new ClientEventSocket(wait4Reply);
        // Attempt Connect
        Future<Session> fut = client.connect(socket, uri);
        // Wait for Connect
        Session session = fut.get();
        session.getRemote().sendString("Hello", null);
        
        assertTrue(wait4Reply.await(10, TimeUnit.SECONDS));
        
        session.close();
    }

    public URI getURI(Server server) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return URI.create(
                String.format(Locale.ROOT, "%s://%s:%d/events/", "ws", addr.getHostAddress(), connector.getLocalPort()));
    }

}
