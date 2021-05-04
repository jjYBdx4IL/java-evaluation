package tests.javax.ws.rs;

import org.junit.After;
import org.junit.Before;

public class RestTestBase {

    private RestServer server = null;

    @Before
    public void beforeClass() throws Exception {
        server = new RestServer();
        server.start();
    }

    @After
    public void afterClass() throws Exception {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    protected RestServer getServer() {
        return server;
    }

}
