package com.google.api.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Env;
import com.github.jjYBdx4IL.utils.env.Surefire;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Demonstration on how to verify/identify a Google user account for login
 * purposes.
 *
 * This test requires Google API application credentials. You need to go to
 * their developer console and create an app.
 *
 * See https://developers.google.com/identity/protocols/OpenIDConnect
 *
 * To get your API credentials, go straight to
 * https://console.developers.google.com/apis/credentials - you don't need to
 * select any APIs, just API Manager -> Credentials -> Create -> Oauth client ID
 * -> Web Application.
 *
 * This example expects a properties file called googleOauth2Client.properties
 * in the app config dir determined via
 * {@link Env#getConfigDir(java.lang.Class)}, supplied with this test class. The
 * contents are:
 *
 * <pre>
 * {@code
 * clientId=...
 * clientSecret=...
 * clientRedirectUri=...
 * }
 * </pre>
 *
 * All of these parameters must match the ones specified in the Google developer
 * console.
 *
 * @author jjYBdx4IL
 */
public class GoogleOauth2ExampleTest extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleOauth2ExampleTest.class);
    private static final File GOOGLE_OAUTH2_CLIENT_CFG_FILE = new File(Env.getConfigDir(GoogleOauth2ExampleTest.class),
            "googleOauth2Client.properties");
    private final Properties oauth2ClientConfig = new Properties();
    private Server server = null;
    private final Properties userSession = new Properties();
    private GoogleAuthorizationCodeFlow codeFlow = null;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    @After
    public void after() throws Exception {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @Test
    public void test() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());

        assertTrue(GOOGLE_OAUTH2_CLIENT_CFG_FILE.getAbsolutePath() + " not found, skipping test unit",
                GOOGLE_OAUTH2_CLIENT_CFG_FILE.exists());

        try (InputStream is = new FileInputStream(GOOGLE_OAUTH2_CLIENT_CFG_FILE)) {
            oauth2ClientConfig.load(is);
        }

        // this is per login:
        String stateSecret = RandomStringUtils.randomAlphanumeric(16, 17);
        userSession.put("GoogleAuthStateSecret", stateSecret);

        // set up the code flow support instance:
        codeFlow = new GoogleAuthorizationCodeFlow(new NetHttpTransport(), new JacksonFactory(),
                oauth2ClientConfig.getProperty("clientId"), oauth2ClientConfig.getProperty("clientSecret"),
                Arrays.asList(new String[] { "openid", "email" }));

        // first step, send user to Google login form (ie. by setting href for
        // the Google Sign-In image button):
        URL redirectUrl = new URL(oauth2ClientConfig.getProperty("clientRedirectUri"));
        // set state to something random to verify in the callback
        String loginUrl = codeFlow.newAuthorizationUrl().setState(stateSecret).setRedirectUri(redirectUrl.toString())
                .build();
        LOG.info("codeFlow.newAuthorizationUrl()...build(): " + loginUrl);

        // start the server handling the callback
        server = new Server(redirectUrl.getPort());
        server.setHandler(this);
        server.start();

        // we don't use a login page in this example, we send the user straight
        // to the Google login form:
        Desktop.getDesktop().browse(new URL(loginUrl).toURI());

        // wait for test (callback) to complete
        countDownLatch.await();
        // let the browser download the redirect page
        Thread.sleep(1000);
        server.stop();
        server.join();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

        // second step: the user gets redirected back to a page on our server,
        // specified in clientRedirectUri:
        URL redirectUrl = new URL((String) oauth2ClientConfig.get("clientRedirectUri"));
        if (!redirectUrl.getPath().equals(target)) { // ignore all requests not
                                                     // related to the callback
            return;
        }

        // from the redirect we obtain the code and verify the state secret for
        // security:
        String code = baseRequest.getParameter("code");
        String state = baseRequest.getParameter("state");
        LOG.info(String.format(Locale.ROOT, "code=%s, state=%s", code, state));
        assertNotNull(code);
        assertNotNull(state);
        assertEquals(userSession.get("GoogleAuthStateSecret"), state);

        // third step: now we can use (once only) the code to get and verify the
        // user's verified Google ID
        // contained in the token response:
        GoogleTokenResponse tokenResponse = codeFlow.newTokenRequest(code).setRedirectUri(redirectUrl.toString())
                .execute(); // POST request to Google token API
        Payload payload = tokenResponse.parseIdToken().getPayload();
        LOG.info("user info payload: " + payload);
        assertNotNull(payload);

        LOG.info("unique google user id: " + payload.getSubject());
        LOG.info("user email: " + payload.getEmail());
        LOG.info("user email verified: " + payload.getEmailVerified());

        assertNotNull(payload.getSubject());
        assertNotNull(payload.getEmail());
        assertNotNull(payload.getEmailVerified());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");
        response.getWriter()
                .print(String.format(Locale.ROOT,
                        "<html><body><h2>You have been identified as %s "
                                + "(verified=%s, subject id=%s)</h2></body></html>",
                        StringEscapeUtils.escapeHtml(payload.getEmail()), payload.getEmailVerified().toString(),
                        payload.getSubject()));

        baseRequest.setHandled(true);

        countDownLatch.countDown();
    }

}
