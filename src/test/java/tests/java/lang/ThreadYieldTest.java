package tests.java.lang;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ThreadYieldTest {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadYieldTest.class);

    private static final int N_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final int N_THREADS = N_PROCESSORS * 2 + 2;
    private static final int N_EGOISTIC_THREADS = N_PROCESSORS * 2;
    private static final long DURATION_MILLIS = 2 * 1000L;
    private static final long MAX_ACCEPTABLE_YIELD_MILLIS = 100L;

    private final AtomicLong threadCount = new AtomicLong(0L);
    private final List<Long> maxYieldTimes = new ArrayList<>();
    private final Runnable runnable = new Runnable() {
        @SuppressWarnings("static-access")
        @Override
        public void run() {
            long stopTime = System.currentTimeMillis() + DURATION_MILLIS;
            long maxYieldTime = 0L;
            boolean doYield = threadCount.getAndIncrement() >= N_EGOISTIC_THREADS;
            long someCounterToAvoidOptimization = 0L;
            while (System.currentTimeMillis() < stopTime) {
                someCounterToAvoidOptimization++;
                if (!doYield) {
                    continue;
                }
                long yieldStart = System.currentTimeMillis();
                Thread.currentThread().yield();
                long yieldTime = System.currentTimeMillis() - yieldStart;
                if (yieldTime > maxYieldTime) {
                    maxYieldTime = yieldTime;
                }
            }
            synchronized (maxYieldTimes) {
                maxYieldTimes.add(maxYieldTime);
            }
            LOG.debug("max yield time: " + maxYieldTime + " ms (" + someCounterToAvoidOptimization + ")");
        }
    };

    /**
     * Test thread starvation due to egoistic execution. Result: calling yield is not necessary (RTFM).
     *
     * @throws InterruptedException
     */
    @Ignore // not working for some reason
    @Test(timeout = 1000L * 180L)
    public void threadYieldTest() throws InterruptedException {
        long timeout = System.currentTimeMillis() + 1000L * 200L;
        long maxYieldTime = 0L;
        do {
            maxYieldTimes.clear();
            threadCount.set(0L);
            List<Thread> threads = new ArrayList<>();
            for (int i = 0; i < N_THREADS; i++) {
                LOG.debug("starting thread no " + i);
                Thread t = new Thread(runnable);
                t.start();
                threads.add(t);
            }
            for (int i = 0; i < N_THREADS; i++) {
                LOG.debug("waiting for thread no " + 1);
                threads.get(i).join();
            }
            for (Long l : maxYieldTimes) {
                if (l > maxYieldTime) {
                    maxYieldTime = l;
                }
            }
        } while (maxYieldTime > MAX_ACCEPTABLE_YIELD_MILLIS && System.currentTimeMillis() < timeout);
    }
}
