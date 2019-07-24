package tests.java.net;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import testgroup.RequiresIsolatedVM;

@Category(RequiresIsolatedVM.class)
public class ServerClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(ServerClientTest.class);

    private final CountDownLatch countdown = new CountDownLatch(1);

    @Test
    public void test() throws IOException, InterruptedException {
        try (final ServerSocket ss = new ServerSocket(0)) {
            LOG.info("listening at: " + ss.getLocalSocketAddress());

            new Thread() {
                @Override
                public void run() {
                    // accept() blocks until it gets a new connection. For
                    // graceful shutdown, one may set a timeout
                    // or simply close the ServerSocket.
                    try (Socket socket = ss.accept();
                        // new ObjectInputStream(..) will block until it
                        // receives a stream header
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                        // send header so the client can open the object input
                        // stream
                        oos.flush();
                        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                            SomeData data = (SomeData) ois.readObject();
                            if (data.integer == 3 && data.strings.get(0).equals("test")) {
                                countdown.countDown();
                            }
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        LOG.error("", e);
                    }
                }
            }.start();

            try (Socket client = new Socket(InetAddress.getLoopbackAddress(), ss.getLocalPort());
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream())) {
                // send header so the server can open the object input stream
                oos.flush();
                try (ObjectInputStream ois = new ObjectInputStream(client.getInputStream())) {
                    SomeData data = new SomeData();
                    data.integer = 3;
                    data.strings.add("test");
                    oos.writeObject(data);
                }
            }

            assertTrue(countdown.await(10, TimeUnit.SECONDS));
        }
    }

    static class SomeData implements Serializable {
        private static final long serialVersionUID = 1L;
        public Integer integer = null;
        public List<String> strings = new ArrayList<>();
    }
}
