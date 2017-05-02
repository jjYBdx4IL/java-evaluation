package twitter4j;

import com.github.jjYBdx4IL.utils.env.Surefire;

import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static twitter4j.TwitterApp.APP_OAUTH_SECRET;
import static twitter4j.TwitterApp.APP_OAUTH_TOKEN;

public class GetUserPermissionsTest {

    private static final Logger LOG = LoggerFactory.getLogger(GetUserPermissionsTest.class);
    
    @Test
    public void testGetUserPermissions() throws IOException, TwitterException {
        assumeTrue(Surefire.isSingleTestExecution());
        
        String token = null;
        String tokenSecret = null;
        
        try (GetUserPermissions perms = new GetUserPermissions(APP_OAUTH_TOKEN, APP_OAUTH_SECRET)) {
            perms.openBrowserForTwitterAppAuth();
            perms.waitForUserReturn();
            token = perms.getAccessToken().getToken();
            tokenSecret = perms.getAccessToken().getTokenSecret();
            LOG.info(token + " " + tokenSecret);
        }
        
        Twitter twitter = getUserTwitter(token, tokenSecret);
        List<Status> statuses = twitter.getUserTimeline();
        LOG.info("Showing user timeline. " + statuses.size());
        for (Status status : statuses) {
            LOG.info(status.getUser().getName() + ":" +
                               status.getText());
        }
        
    }

    private Twitter getUserTwitter(String token, String tokenSecret) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(APP_OAUTH_TOKEN).setOAuthConsumerSecret(APP_OAUTH_SECRET)
                .setOAuthAccessToken(token).setOAuthAccessTokenSecret(tokenSecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        return twitter;
    }
    
}
