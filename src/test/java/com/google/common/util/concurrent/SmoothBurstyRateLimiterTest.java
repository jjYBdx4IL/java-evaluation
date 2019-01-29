package com.google.common.util.concurrent;

import static org.junit.Assert.assertTrue;

import com.google.common.util.concurrent.SmoothBurstyRateLimiter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmoothBurstyRateLimiterTest {

    private static final Logger LOG = LoggerFactory.getLogger(SmoothBurstyRateLimiterTest.class);
    RateLimiter rl = SmoothBurstyRateLimiter.create(1.0, 20.0);
    
    @Test
    public void test() throws InterruptedException {
        Thread.sleep(1000);
        assertTrue(nextDelayMs() < 500);
        assertTrue(nextDelayMs() < 500);
        assertTrue(nextDelayMs() > 500);
    }
    
    public long nextDelayMs() {
        long start = System.currentTimeMillis();
        rl.acquire();
        long duration = System.currentTimeMillis() - start;
        LOG.info("rate limiter stalled for " + duration + " ms");
        return duration;
    }
}
