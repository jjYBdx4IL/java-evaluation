package tests.javax.ws.rs;

import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.io.IoUtils;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;

public class HelloTest {

    private static final Logger LOG = LoggerFactory.getLogger(HelloTest.class);

    public URL getURL(Server server, String path) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(String.format(Locale.ROOT, "%s://%s:%d%s", "http", addr.getHostAddress(),
                connector.getLocalPort(), path));
    }

    @Test
    public void testRESTful() throws Exception {
        Server server = new Server(0);

        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[] { contexts, new DefaultHandler() });
        server.setHandler(handlers);

        // "/"
        ServletContextHandler rootContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        rootContext.setContextPath("/");

        // "/rest/"
        ResourceConfig config = new ResourceConfig();
        config.packages(Hello.class.getPackage().getName());
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        ServletContextHandler restContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        restContext.setContextPath("/rest/");
        restContext.addServlet(servlet, "/*");

        contexts.setHandlers(new Handler[] { restContext, rootContext });

        server.start();

        URL serverURL = getURL(server, "/rest/hello");
        LOG.info("server URL: " + serverURL);
        String pageContents = IOUtils.toString(serverURL, "ASCII");
        LOG.info("test page contents: " + pageContents);
        assertEquals("<html> <title>Hello Jersey</title><body><h1>Hello Jersey</body></h1></html> ", pageContents);

        assertEquals("Hello Jersey", IoUtils.toString(serverURL, "text/plain; charset=ASCII"));

        server.stop();
    }

}
