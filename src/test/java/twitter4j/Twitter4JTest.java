package twitter4j;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.env.Surefire;
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
    public void testGetTimeline() throws Exception {
        List<Status> statuses = twitter.getHomeTimeline();
        LOG.info("Showing home timeline.");
        for (Status status : statuses) {
            LOG.info(status.getUser().getName() + ":" +
                               status.getText());
        }
    }
}
