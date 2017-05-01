package twitter4j;

import java.io.IOException;

import com.github.jjYBdx4IL.utils.AbstractConfig;
import com.github.jjYBdx4IL.utils.awt.AWTUtils;

import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author jjYBdx4IL
 */
public class Twitter4JTestConfig extends AbstractConfig {

    private static final String APP_OAUTH_TOKEN = "IwWpYMEdV0eZHYxwznNNr5fh5";
    private static final String APP_OAUTH_SECRET = "ZzBkuVmNqL7gGWZAsUKDsqNagXtP1l6gpyM48PNBQ564d8MVae";

    public String token = "";
    public String tokenSecret = "";

    public Twitter4JTestConfig() {
        super(Twitter4JTest.class, false);
    }

    protected void readDidntFindConfigFile() {
        showConfigUI();
    }

    private void showConfigUI() {
        Twitter4JAuthFrame frame = new Twitter4JAuthFrame();
        AWTUtils.showFrameAndWaitForCloseByUser(frame);
        try {
            read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void postprocess() {
        if (token == null || token.isEmpty() || tokenSecret == null || tokenSecret.isEmpty()) {
            showConfigUI();
        }

        if (token == null || token.isEmpty() || tokenSecret == null || tokenSecret.isEmpty()) {
            throw new RuntimeException("config failed");
        }
    }

    // app-level authorized twitter object
    public static Twitter getTwitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(APP_OAUTH_TOKEN).setOAuthConsumerSecret(APP_OAUTH_SECRET);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        return twitter;
    }

    // user-level authorized twitter object
    public Twitter getUserTwitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(APP_OAUTH_TOKEN).setOAuthConsumerSecret(APP_OAUTH_SECRET)
                .setOAuthAccessToken(token).setOAuthAccessTokenSecret(tokenSecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        return twitter;
    }

}
