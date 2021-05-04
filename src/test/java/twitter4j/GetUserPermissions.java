package twitter4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetUserPermissions extends AbstractHandler implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(GetUserPermissions.class);

    private final String appOAuthToken;
    private final String appOAuthTokenSecret;
    private String oAuthVerifier = null;
    private volatile AccessToken accessToken = null;
    private Server server = null;

    private Twitter twitter = null;
    private RequestToken requestToken = null;
    private CountDownLatch latch = new CountDownLatch(1);

    public GetUserPermissions(String appOAuthToken, String appOAuthTokenSecret) throws IOException {
        this.appOAuthToken = appOAuthToken;
        this.appOAuthTokenSecret = appOAuthTokenSecret;
    }

    @Override
    public void close() throws IOException {
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

        if (!target.equals("/")) {
            return;
        }
        
        // match request with callback request 
        String oAuthToken = baseRequest.getParameter("oauth_token");
        assertNotNull(oAuthToken);
        LOG.info("oAuthToken: " + oAuthToken);
        assertEquals(requestToken.getToken(), oAuthToken);
        
        // transform oauth token into access token
        oAuthVerifier = baseRequest.getParameter("oauth_verifier");
        assertNotNull(oAuthVerifier);
        LOG.info("oAuthVerifier: " + oAuthVerifier);
        try {
            accessToken = twitter.getOAuthAccessToken(requestToken, oAuthVerifier);
            LOG.info("accessToken: " + accessToken);
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }        
        
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");
        response.getWriter()
            .print("<html><body><h2>Token received. Close this browser window to continue.</h2></body></html>");

        baseRequest.setHandled(true);
        latch.countDown();
    }

    private URL getReturnURL() throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        URL returnUrl = new URL("http", "localhost", connector.getLocalPort(), "");
        LOG.info("return url: " + returnUrl.toExternalForm());
        return returnUrl;
    }

    public void openBrowserForTwitterAppAuth() {
        try {
            server = new Server(TwitterApp.LOCALHOST_CALLBACK_PORT);
            server.setHandler(this);
            try {
                server.start();
            } catch (Exception e) {
                throw new IOException(e);
            }
            twitter = getTwitter();

            // requestToken = twitter.getOAuthRequestToken("oob");
            requestToken = twitter.getOAuthRequestToken(getReturnURL().toExternalForm());
            assertNotNull(requestToken);
            LOG.info("token secret: " + requestToken.getTokenSecret());
            String authURL = requestToken.getAuthorizationURL();
            LOG.info("new auth URL: " + authURL);
            Desktop.getDesktop().browse(new URL(authURL).toURI());
        } catch (TwitterException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Twitter getTwitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(appOAuthToken).setOAuthConsumerSecret(appOAuthTokenSecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        return twitter;
    }

    // doesnt account for user not accepting authorization yet
    void waitForUserReturn() throws InterruptedException {
        latch.await();
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

}
