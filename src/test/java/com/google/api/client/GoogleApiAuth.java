package com.google.api.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.github.jjYBdx4IL.utils.env.Env;
import com.github.jjYBdx4IL.utils.io.FilePermUtils;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.auth.oauth2.UserCredentials;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * As of google client v1.28.1 there is an explicit dependency on jetty *6* for
 * the convenience class LocalServerReceiver which runs a local server on a free
 * port to authenticate using Google OAuth2. That class is in conflict with
 * recent Jetty versions.
 * 
 * The config/data directory used for persistence of credentials and
 * configuration data is located at:
 * 
 * '$HOME/AppData/Local/$fully-qualified-class-name/' on Windows and
 * '$HOME/.config/$fully-qualified-class-name/' else.
 * 
 * @author jjYBdx4IL
 */
public class GoogleApiAuth extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleApiAuth.class);
    private final File configDir;
    private final File clientIdFile;
    // delete the files in this directory if you get permission problems when
    // tinkering around with this test
    private final File credStore;
    private Server server = null;
    private GoogleAuthorizationCodeFlow codeFlow = null;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private URL redirectUrl = null;
    private String stateSecret = null;
    private String datastoreUser = null;
    private GoogleClientSecrets clientSecrets = null;

    /**
     * 
     * @param clazz
     *            defines the config directory
     */
    public GoogleApiAuth(Class<?> clazz) {
        configDir = Env.getConfigDir(clazz);
        clientIdFile = new File(configDir, "client_id.json");
        credStore = new File(configDir, "credentials");
    }

    private GoogleClientSecrets getClientSecrets() throws IOException {
        if (clientSecrets != null) {
            return clientSecrets;
        }
        
        if (!clientIdFile.exists()) {
            throw new FileNotFoundException(clientIdFile
                + " not found. Go to: https://console.developers.google.com/apis"
                + " and select the desired API service in Google's API library, then create credentials and download"
                + " them in JSON format to the aforementioned file location.");
        }

        // load client secrets
        clientSecrets = GoogleClientSecrets.load(
            JacksonFactory.getDefaultInstance(),
            new InputStreamReader(new FileInputStream(clientIdFile)));
        
        return clientSecrets;
    }
    
    /**
     * NOT thread-safe.
     * 
     * https://developers.google.com/api-client-library/java/google-oauth-java-client/oauth2
     * 
     * @param datastoreUser
     *            if running on a web server, this could be some www user id
     * @return the credential reflecting the successful authorization
     * @throws Exception
     *             if there is some problem
     */
    public Credential authorize(List<String> scopes, String _datastoreUser)
        throws Exception {
        this.datastoreUser = _datastoreUser;

        if (!configDir.exists()) {
            configDir.mkdirs();
            FilePermUtils.setOwnerAccessOnlyNonExec(configDir);
        }

        // load client secrets
        GoogleClientSecrets clientSecrets = getClientSecrets();

        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(credStore);
        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore("datastore");

        // set up the code flow support instance:
        codeFlow = new GoogleAuthorizationCodeFlow.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            clientSecrets,
            scopes)
                .setCredentialDataStore(datastore)
                .build();

        Credential credential = codeFlow.loadCredential(datastoreUser);
        LOG.debug("stored credential: " + credential);
        if (credential != null) {
            LOG.debug("loaded credential from: " + credStore.getAbsolutePath() + " : " + datastore.getId());
            LOG.debug("stored credential access token: " + credential.getAccessToken());
            LOG.debug("stored credential refresh token: " + credential.getRefreshToken());
            LOG.debug("stored credential expires in secs: " + credential.getExpiresInSeconds());
        }

        // do we need to authenticate?
        if (credential != null) {
            // no, re-use stored credential
            checkArgument(!credential.getRefreshListeners().isEmpty());
            // uncomment to force an authentication error (expired tokens)
            // credential.setRefreshToken("asldkjas");
            // credential.setAccessToken("asldkjas");
            return credential;
        }
        // the brower-based authentication needs to be done only once. It will
        // give use a refresh token
        // that will be valid for practically forever
        // (https://developers.google.com/identity/protocols/OAuth2 towards
        // the end of the page) and through which the google client will
        // automatically retrieve short-lived
        // access tokens. If the refresh token ever gets invalidated, it seems
        // that the credential has to be
        // removed manually removed from or replaced in the data store.
        // Optimally, a user application would wrap
        // Google API calls in try-catch clauses to detect authentication
        // issues, and then restart the authentication
        // mechanism to replace the credential in the data store. However, I
        // don't know any determinate way to detect
        // such an authentication issue. It's probably best to add a hint to
        // caught
        // com.google.api.client.auth.oauth2.TokenResponseException that tells
        // the user to use an application-supplied
        // option to force re-authentication.

        // this is per auth process, serving as an authorization process
        // identifier:
        stateSecret = RandomStringUtils.randomAlphanumeric(16, 17);

        // start the server handling the callback
        server = new Server(0);
        server.setHandler(this);
        server.start();

        try {
            // first step, send user to Google login form (ie. by setting href
            // for
            // the Google Sign-In image button):
            redirectUrl = new URL("http://localhost:" + server.getURI().getPort() + "/");
            LOG.debug("redirectUrl: " + redirectUrl);
            // set state to something random to verify in the callback
            String loginUrl = codeFlow.newAuthorizationUrl().setState(stateSecret)
                .setRedirectUri(redirectUrl.toString())
                .build();
            LOG.debug("codeFlow.newAuthorizationUrl()...build(): " + loginUrl);

            // we don't use a login page in this example, we send the user
            // straight
            // to the Google login form:
            Desktop.getDesktop().browse(new URL(loginUrl).toURI());

            // wait for test (callback) to complete
            countDownLatch.await();
            // be nice and let the browser download the redirect page
            Thread.sleep(1000);
            
            credential = codeFlow.loadCredential(datastoreUser);
            checkArgument(!credential.getRefreshListeners().isEmpty());
            return credential;
        } catch (Exception ex) {
            server.stop();
            server.join();
            throw new Exception(ex);
        }
    }
    
    public UserCredentials toUserCredentials(Credential credential) throws IOException {
        return UserCredentials.newBuilder()
            .setClientId(getClientSecrets().getDetails().getClientId())
            .setClientSecret(getClientSecrets().getDetails().getClientSecret())
            .setRefreshToken(credential.getRefreshToken())
            .build();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        LOG.debug(String.format(Locale.ROOT, "handle(%s, ...)", target));

        // second step: the user gets redirected back to a page on our server,
        // specified in clientRedirectUri:
        if (!redirectUrl.getPath().equals(target)) { // ignore all requests not
                                                     // related to the callback
            return;
        }

        baseRequest.setHandled(true);
        countDownLatch.countDown();

        String error = baseRequest.getParameter("error");
        LOG.warn("error: " + error);
        if (error != null && !error.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain");
            response.getWriter()
                .print(String.format(Locale.ROOT, "error: %s", error));
            return;
        }

        try {
            // from the redirect we obtain the code and verify the state secret
            // for
            // security:
            String code = baseRequest.getParameter("code");
            String state = baseRequest.getParameter("state");
            LOG.debug(String.format(Locale.ROOT, "code=%s, state=%s", code, state));
            checkNotNull(code);
            checkNotNull(state);
            checkArgument(state.equals(stateSecret));

            // third step: now we can use (once only) the code to get and verify
            // the
            // user's verified Google ID
            // contained in the token response:
            GoogleTokenResponse tokenResponse = codeFlow.newTokenRequest(code).setRedirectUri(redirectUrl.toString())
                .execute(); // POST request to Google token API

            checkNotNull(tokenResponse);
            codeFlow.createAndStoreCredential(tokenResponse, datastoreUser);

            if (tokenResponse.getIdToken() == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/plain");
                response.getWriter()
                    .print("OK");
                return;
            }

            // only works with openid, email scopes
            Payload payload = tokenResponse.parseIdToken().getPayload();
            LOG.debug("user info payload: " + payload);
            checkNotNull(payload);

            LOG.debug("unique google user id: " + payload.getSubject());
            LOG.debug("user email: " + payload.getEmail());
            LOG.debug("user email verified: " + payload.getEmailVerified());

            checkNotNull(payload.getSubject());
            checkNotNull(payload.getEmail());
            checkNotNull(payload.getEmailVerified());

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
            response.getWriter()
                .print(String.format(Locale.ROOT,
                    "<html><body><h2>You have been identified as %s "
                        + "(verified=%s, subject id=%s)</h2></body></html>",
                    StringEscapeUtils.escapeHtml(payload.getEmail()), payload.getEmailVerified().toString(),
                    payload.getSubject()));
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            response.getWriter().println("error");
            LOG.error("", ex);
        }
    }
}
