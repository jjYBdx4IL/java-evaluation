package tests.java.util.concurrent.atomic;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings("all")
public class AtomicTypesTest {

    private static final Logger LOG = LoggerFactory.getLogger(AtomicTypesTest.class);
    static long nonAtomicLong;

    @Ignore
    @Test
    public void testAtomLong() throws InterruptedException {
        final AtomicLong l = new AtomicLong(0L);
        nonAtomicLong = 0L;
        
        final long nLoopsPerThread = 1000L*1000L;
        int nThreads = 4 * Runtime.getRuntime().availableProcessors();

        Thread[] pool = new Thread[nThreads];
        for (int i = 0; i < pool.length; i++) {
            pool[i] = new Thread() {
                @Override
                public void run() {
                    for (long i = 0; i < nLoopsPerThread; i++) {
                        l.incrementAndGet();
                        nonAtomicLong++;
                    }
                }
            };
        }
        for (Thread t : pool) {
            t.start();
        }
        for (Thread t : pool) {
            t.join();
        }

        assertEquals(nThreads * nLoopsPerThread, l.get());
        assertTrue(nThreads * nLoopsPerThread > nonAtomicLong);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
}
