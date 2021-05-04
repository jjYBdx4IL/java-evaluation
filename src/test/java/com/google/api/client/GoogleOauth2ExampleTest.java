package com.google.api.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.google.api.client.auth.oauth2.Credential;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 *
 * @author jjYBdx4IL
 */
public class GoogleOauth2ExampleTest {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleOauth2ExampleTest.class);

    @Test
    public void test() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());

        GoogleApiAuth auth = new GoogleApiAuth(GoogleOauth2ExampleTest.class);
        Credential credential = auth.authorize(Arrays.asList("openid", "email"), "user");
        assertNotNull(credential);
        LOG.info(credential.getAccessToken());
    }

}
