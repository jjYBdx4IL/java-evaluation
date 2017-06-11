package com.google.api.client;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstration on how to verify/identify a Google user account for login purposes.
 *
 * This test requires Google API application credentials. You need to go to their developer console and create an app.
 *
 * See https://developers.google.com/identity/protocols/OpenIDConnect
 *
 * @author jjYBdx4IL
 */
public class GoogleOauth2ExampleTest {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleOauth2ExampleTest.class);

    @Test
    public void test() throws IOException {
        // this goes into some configuration file:
        String clientId = "clientId";
        String clientSecret = "clientSecret";
        
        // this is per login:
        String stateSecret = RandomStringUtils.randomAlphanumeric(16, 17);
        saveInUserSession("GoogleAuthStateSecret", stateSecret);
        
        GoogleAuthorizationCodeFlow codeFlow = new GoogleAuthorizationCodeFlow(
                new NetHttpTransport(),
                new JacksonFactory(),
                clientId,
                clientSecret,
                Arrays.asList(new String[]{"openid", "email"}));

        // first step, user gets redirected to this url (GET):
        LOG.info("codeFlow.newAuthorizationUrl()...build(): "
                + codeFlow.newAuthorizationUrl()
                        .setState(stateSecret) // set state to something random to verify in the callback
                        .setRedirectUri("http://my.website.com/callback")
                        .build());

        // our callback (ie servlet) receives the token which we use to get an access_token and ID information
        // about the user by doing a POST request:
        LOG.info("codeFlow.newTokenRequest(): " + codeFlow.newTokenRequest("code"));
        
        /**
         * Execute the token POST request like this:
         *
         * GoogleTokenResponse response = codeFlow.newTokenRequest("code").execute();
         *
         * And you will get the Google user's unique Google id via:
         *
         * response.parseIdToken().getPayload().getSubject();
         * 
         * And the user's email plus verification state of it:
         * 
         * response.parseIdToken().getPayload().getEmail();
         * response.parseIdToken().getPayload().getEmailVerified();
         */
    }

    private void saveInUserSession(String key, String value) {
    }
}
