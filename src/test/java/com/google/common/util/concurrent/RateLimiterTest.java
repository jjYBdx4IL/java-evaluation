package com.google.common.util.concurrent;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RateLimiterTest {

    RateLimiter rl = RateLimiter.create(1.0);
    
    @Test
    public void test() {
        assertTrue(nextDelayMs() < 500);
        assertTrue(nextDelayMs() > 500);
    }
    
    public long nextDelayMs() {
        long start = System.currentTimeMillis();
        rl.acquire();
        return System.currentTimeMillis() - start;
    }
}
