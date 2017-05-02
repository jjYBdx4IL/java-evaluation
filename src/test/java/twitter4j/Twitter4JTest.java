package twitter4j;

import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.env.Surefire;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 *
 * @author jjYBdx4IL
 */
public class Twitter4JTest {

    private static final Logger LOG = LoggerFactory.getLogger(Twitter4JTest.class);

    private static Twitter twitter = null;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        Twitter4JTestConfig config = new Twitter4JTestConfig();
        config.read();
        twitter = config.getUserTwitter();
    }
    
    @Before
    public void before() {
        assumeTrue(Surefire.isSingleTestExecution());
    }
    
    @Test
    public void testGetHomeTimeline() throws Exception {
        List<Status> statuses = twitter.getHomeTimeline();
        LOG.info("Showing home timeline.");
        for (Status status : statuses) {
            LOG.info(status.getUser().getName() + ":" +
                               status.getText());
        }
    }
    
    @Test
    public void testGetUserTimeline() throws Exception {
        List<Status> statuses = twitter.getUserTimeline();
        LOG.info("Showing user timeline. " + statuses.size());
        for (Status status : statuses) {
            LOG.info(status.getUser().getName() + ":" +
                               status.getText());
        }
    }
    
    @Test
    public void testGetUserTimelineWithPaging() throws Exception {
        // no paging:
        List<Status> statuses = twitter.getUserTimeline();
        LOG.info("Showing user timeline. " + statuses.size());
        for (Status status : statuses) {
            LOG.info(status.getUser().getName() + ":" +
                               status.getText());
        }
        
        // with paging:
        List<Status> statuses2 = twitter.getUserTimeline(new Paging(1, 1));
        assertEquals(1, statuses2.size());
        assertEquals(statuses.get(0).getId(), statuses2.get(0).getId());
        assertEquals(statuses.get(0).getText(), statuses2.get(0).getText());
    }
    
    @Test
    public void testTweet() throws Exception {
        String msg = "test: " + System.currentTimeMillis() + "." + new Random().nextInt();
        twitter.updateStatus(msg);
        
        List<Status> statuses = twitter.getUserTimeline(new Paging(1,1));
        assertEquals(msg, statuses.get(0).getText());
    }
    
}
