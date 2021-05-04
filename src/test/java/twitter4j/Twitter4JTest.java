package twitter4j;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * http://twitter4j.org/en/index.html
 *
 * @author jjYBdx4IL
 */
public class Twitter4JTest {

    static Twitter twitter = null;

    @Before
    public void before() throws FileNotFoundException, IOException {
        assumeTrue(Surefire.isSingleTestExecution());

        Twitter4JTestConfig config = new Twitter4JTestConfig();
        config.read();
        twitter = config.getUserTwitter();
    }

    @Test
    public void testStream() throws InterruptedException {
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                out.println("onStatus " + status);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                out.println("onDeletionNotice" + statusDeletionNotice);
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                out.println("onTrackLimitationNotice " + numberOfLimitedStatuses);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                out.println("onScrubGeo " + userId + " " + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                out.println("onStallWarning " + warning);
            }
        };
        TwitterStream twitterStream = new TwitterStreamFactory(twitter.getConfiguration()).getInstance();
        twitterStream.addListener(listener);
        twitterStream.sample("en");
        Thread.sleep(1000000L);

    }

    static abstract class Consumer {
        abstract boolean consume(Status status) throws Exception;
    }

    void sample(final Consumer consumer) {
        final CountDownLatch latch = new CountDownLatch(1);
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                boolean done = true;
                try {
                    done = !consumer.consume(status);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
                if (done) {
                    latch.countDown();
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

            @Override
            public void onException(Exception ex) {
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
            }

            @Override
            public void onStallWarning(StallWarning warning) {
            }
        };
        TwitterStream twitterStream = new TwitterStreamFactory(twitter.getConfiguration()).getInstance();
        twitterStream.addListener(listener);
        twitterStream.sample("en");
        try {
            latch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        twitterStream.cleanUp();
    }

    @Test
    public void testConversation() throws TwitterException {
        Status status = twitter.lookup(1099466905713328128L).get(0);
        out.println(status.getUser().getName() + ": " + status.getText());
        while (status.getInReplyToStatusId() != -1) {
            status = twitter.lookup(status.getInReplyToStatusId()).get(0);
            out.println("in reply to: " + status.getUser().getName() + ": " + status.getText());
        }
    }

    @Test
    public void testLike() throws TwitterException, InterruptedException {
        sample(new Consumer() {

            @Override
            boolean consume(Status tweet) throws Exception {
                if (!tweet.isFavorited()) {
                    out.println("liking: " + twitter.createFavorite(tweet.getId()));
                    return false;
                }
                return true;
            }
        });
    }

    @Test
    public void testSearch() throws TwitterException {
        Query query = new Query();
        query.setCount(100);
        query.setLang("de");
        query.setQuery("best");
        QueryResult result = twitter.search(query);
        result.getTweets().forEach(v -> out.println(v));
    }

    @Test
    public void testGetRateLimitStatus() throws TwitterException {
        twitter.getRateLimitStatus().forEach((k, v) -> {
            if (v.getSecondsUntilReset() != 899 || v.getRemaining() == 0) {
                out.println(v + " " + k);
            }
        });
    }

    @Test
    public void testFollow() throws Exception {
        int page = 0;
        while (true) {
            List<Status> statuses = twitter.getHomeTimeline(new Paging(page));
            out.println(">>> Showing home timeline, page=" + page);
            page++;
            for (Status status : statuses) {
                out.println(status.getCreatedAt() + " " + status.getLang() + " "
                    + status.getUser().getName() + ":" + status.getText());
                if (twitter.getId() == status.getUser().getId()) {
                    continue;
                }
                out.println("now a friend of: " + twitter.createFriendship(status.getUser().getId()));
                return;
            }
        }
    }

    @Test
    public void testGetUserTimeline() throws Exception {
        List<Status> statuses = twitter.getUserTimeline();
        out.println(">>> Showing user timeline. " + statuses.size());
        for (Status status : statuses) {
            out.println(status.getUser().getName() + ":" +
                status.getText());
        }
    }

    @Test
    public void testDeleteUserTimeline() throws Exception {
        List<Status> statuses = twitter.getUserTimeline();
        out.println(">>> Showing user timeline. n = " + statuses.size());
        long n = 0;
        for (Status status : statuses) {
            twitter.destroyStatus(status.getId());
            n++;
        }
        out.println("deleted " + n + " tweets");
    }

    @Test
    public void testGetUserTimelineWithPaging() throws Exception {
        // no paging:
        List<Status> statuses = twitter.getUserTimeline();
        out.println(">>> Showing user timeline. " + statuses.size());
        for (Status status : statuses) {
            out.println(status.getUser().getName() + ":" +
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
        String msg = "test: " + new Date() + "." + new Random().nextInt();
        twitter.updateStatus(msg);

        List<Status> statuses = twitter.getUserTimeline(new Paging(1, 1));
        assertEquals(msg, statuses.get(0).getText());
    }

}
