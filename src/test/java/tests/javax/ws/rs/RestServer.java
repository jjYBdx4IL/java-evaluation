package tests.javax.ws.rs;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;

class RestServer {

    public static final String REST_CTX_PATH = "/rest/";

    private final Server server;

    public RestServer() {
        server = new Server(0);
    }

    public void start() throws Exception {
        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[] { contexts, new DefaultHandler() });
        server.setHandler(handlers);

        // "/"
        ServletContextHandler rootContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        rootContext.setContextPath("/");

        // "/rest/"
//        ResourceConfig config = new ResourceConfig();
//        config.packages(HelloServiceImpl.class.getPackage().getName());
//        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        ServletHolder servlet = new ServletHolder(new ServletContainer());
        servlet.setInitParameter("javax.ws.rs.Application", RestApp.class.getName());
        ServletContextHandler restContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        restContext.setContextPath(REST_CTX_PATH);
        restContext.addServlet(servlet, "/*");

        contexts.setHandlers(new Handler[] { restContext, rootContext });

        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public URL getURL(String relPath) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(String.format(Locale.ROOT, "%s://%s:%d%s%s", "http", addr.getHostAddress(),
            connector.getLocalPort(), REST_CTX_PATH, relPath));
    }

}
