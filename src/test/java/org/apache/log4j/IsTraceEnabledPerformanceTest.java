package org.apache.log4j;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class IsTraceEnabledPerformanceTest {

    private static final Logger LOG = Logger.getLogger(IsTraceEnabledPerformanceTest.class.getName());
    private static final long test_duration_millis = 4L * 100L;
    private static final String finalStaticString = "test string";
    private static String nonFinalStatisString = "some string";
    private static final int n_per_loop = 1000;

    /**
     * Result: the use of isTraceEnabled() speeds up suppressed logging stmts by a factor of 6 to 7
     * if the log msg gets composited from a final static and a non-final static string.
     */
    
    @Test
    public void test1a() {
        long duration;
        long n_loops = 0;

        long start = System.currentTimeMillis();
        do {
            for (int i = 0; i < n_per_loop; i++) {
                LOG.log(Level.TRACE, "some msg" + finalStaticString);
            }
            n_loops++;
            duration = System.currentTimeMillis() - start;
        } while (duration < test_duration_millis);

        System.out.println("suppressed log stmts per millisecond without isTraceEnabled(): " + ((n_loops * n_per_loop) / duration));
    }

    @Test
    public void test1b() {
        long duration;
        long n_loops = 0;

        long start = System.currentTimeMillis();
        do {
            for (int i = 0; i < n_per_loop; i++) {
                LOG.log(Level.TRACE, "some msg" + nonFinalStatisString);
            }
            n_loops++;
            duration = System.currentTimeMillis() - start;
        } while (duration < test_duration_millis);

        System.out.println("suppressed log stmts per millisecond without isTraceEnabled() and non-final static string: " + ((n_loops * n_per_loop) / duration));
    }

    @Test
    public void test1c() {
        long duration;
        long n_loops = 0;

        long start = System.currentTimeMillis();
        do {
            for (int i = 0; i < n_per_loop; i++) {
                LOG.log(Level.TRACE, "some msg");
            }
            n_loops++;
            duration = System.currentTimeMillis() - start;
        } while (duration < test_duration_millis);

        System.out.println("suppressed log stmts per millisecond without isTraceEnabled() and just one string constant: " + ((n_loops * n_per_loop) / duration));
    }

    @Test
    public void test2a() {
        long duration;
        long n_loops = 0;

        long start = System.currentTimeMillis();
        do {
            for (int i = 0; i < n_per_loop; i++) {
                if (LOG.isTraceEnabled()) {
                    LOG.log(Level.TRACE, "some msg" + finalStaticString);
                }
            }
            n_loops++;
            duration = System.currentTimeMillis() - start;
        } while (duration < test_duration_millis);

        System.out.println("suppressed log stmts per millisecond with isTraceEnabled(): " + ((n_loops * n_per_loop) / duration));
    }

    @Test
    public void test2b() {
        long duration;
        long n_loops = 0;

        long start = System.currentTimeMillis();
        do {
            for (int i = 0; i < n_per_loop; i++) {
                if (LOG.isTraceEnabled()) {
                    LOG.log(Level.TRACE, "some msg" + nonFinalStatisString);
                }
            }
            n_loops++;
            duration = System.currentTimeMillis() - start;
        } while (duration < test_duration_millis);

        System.out.println("suppressed log stmts per millisecond with isTraceEnabled() and non-final static string: " + ((n_loops * n_per_loop) / duration));
    }
    
    @Test
    public void test2c() {
        long duration;
        long n_loops = 0;

        long start = System.currentTimeMillis();
        do {
            for (int i = 0; i < n_per_loop; i++) {
                if (LOG.isTraceEnabled()) {
                    LOG.log(Level.TRACE, "some msg");
                }
            }
            n_loops++;
            duration = System.currentTimeMillis() - start;
        } while (duration < test_duration_millis);

        System.out.println("suppressed log stmts per millisecond with isTraceEnabled() and just one string constant: " + ((n_loops * n_per_loop) / duration));
    }
    
    @Test
    public void test3() {
        long duration;
        long n_loops = 0;

        long start = System.currentTimeMillis();
        do {
            for (int i = 0; i < n_per_loop; i++) {
            }
            n_loops++;
            duration = System.currentTimeMillis() - start;
        } while (duration < test_duration_millis);

        System.out.println("suppressed log stmts per millisecond without any stmt at all!: " + ((n_loops * n_per_loop) / duration));
    }
}
