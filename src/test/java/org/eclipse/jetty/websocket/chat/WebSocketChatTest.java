package org.eclipse.jetty.websocket.chat;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class WebSocketChatTest {

    private Server server = null;
    private Map<WebSocketClient, ClientEventSocket> eventSockets = new HashMap<>();
    private Map<WebSocketClient, Session> sessions = new HashMap<>();
    public static final int NUM_CLIENTS = 10;

    @After
    public void after() throws Exception {
        if (server != null) {
            server.stop();
        }
        for (WebSocketClient client : eventSockets.keySet()) {
            client.stop();
        }
    }

    @Test
    public void testWebSocket() throws Exception {
        server = new Server(0);

        ChatHandler chatHandler = new ChatHandler();
        server.setHandler(chatHandler);

        server.start();
        server.dump(System.err);

        URI uri = getURI(server);

        for (int i = 0; i < NUM_CLIENTS; i++) {
            WebSocketClient client = new WebSocketClient();
            client.start();
            // The socket that receives events
            ClientEventSocket socket = new ClientEventSocket(NUM_CLIENTS);
            // Attempt Connect
            Future<Session> fut = client.connect(socket, uri);
            // Wait for Connect
            Session session = fut.get();
            
            eventSockets.put(client, socket);
            sessions.put(client, session);
        }
        
        for (WebSocketClient client : eventSockets.keySet()) {
            Session sess = sessions.get(client);
            sess.getRemote().sendString("Hello", null);
        }
        
        for (WebSocketClient client : eventSockets.keySet()) {
            ClientEventSocket clientEventSocket = eventSockets.get(client);
            clientEventSocket.await(10L);
            Session sess = sessions.get(client);
            sess.close();
        }
    }

    public URI getURI(Server server) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return URI.create(String.format(Locale.ROOT, "%s://%s:%d/events/", "ws", addr.getHostAddress(),
                connector.getLocalPort()));
    }

}
