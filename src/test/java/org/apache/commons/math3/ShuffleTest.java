package org.apache.commons.math3;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.utils.math.Shuffle;
import com.github.jjYBdx4IL.utils.math.ShuffleTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.util.MathArrays;
import org.apache.log4j.Logger;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ShuffleTest {

    private static final Logger log = Logger.getLogger(ShuffleTest.class.getName());
    private static final int ITERATIONS = 1000;
    private static Random r;

    @Before
    public void beforeTest() {
        r = new Random(0);
    }

    @Test
    public void testPerformance() {
        Shuffle<Integer> s = new Shuffle<>(r);
        long millisPerTest = 1000L;
        long timeout, count, start, perSec;
        Integer[] ia = new Integer[100];
        ShuffleTestBase.init(ia);
        int[] iar = new int[ia.length];
        init(iar);
        List<Integer> il = new ArrayList<>(Arrays.asList(ia));

        start = System.currentTimeMillis();
        timeout = start + millisPerTest;
        count = 0;
        do {
            for (int i = 0; i < ITERATIONS; i++) {
                s.shuffle(ia);
            }
            count += ITERATIONS;
        } while (System.currentTimeMillis() < timeout);
        perSec = count * 1000 / (System.currentTimeMillis() - start);
        log.info(String.format("Shuffe<Integer>.shuffle(...) performance: %d shuffles per second", perSec));

        start = System.currentTimeMillis();
        timeout = start + millisPerTest;
        count = 0;
        do {
            for (int i = 0; i < ITERATIONS; i++) {
                Collections.shuffle(il, r);
            }
            count += ITERATIONS;
        } while (System.currentTimeMillis() < timeout);
        perSec = count * 1000 / (System.currentTimeMillis() - start);
        log.info(String.format("Collections.shuffle(ArrayList<Integer>) performance: %d shuffles per second", perSec));

        start = System.currentTimeMillis();
        timeout = start + millisPerTest;
        count = 0;
        do {
            for (int i = 0; i < ITERATIONS; i++) {
                MathArrays.shuffle(iar, 0, MathArrays.Position.TAIL);
            }
            count += ITERATIONS;
        } while (System.currentTimeMillis() < timeout);
        perSec = count * 1000 / (System.currentTimeMillis() - start);
        log.info(String.format("MathArrays.shuffle(int[]) performance: %d shuffles per second", perSec));
    }

    @Test
    public void test0HEAD() {
        // nothing ever changes
        testAlwaysTrue(0, MathArrays.Position.HEAD, new int[]{0, 0, 0});
    }

    @Test
    public void test1HEAD() {
        // first two positions may change
        testUntilTrue(1, MathArrays.Position.HEAD, new int[]{0, 0, 0});
        testUntilTrue(1, MathArrays.Position.HEAD, new int[]{1, 1, 0});

        // third position never changes
        testAlwaysTrue(1, MathArrays.Position.HEAD, new int[]{-1, -1, 0});
    }

    @Test
    public void test2HEAD() {
        // every position may change
        testUntilTrue(2, MathArrays.Position.HEAD, new int[]{0, 0, 0});
        testUntilTrue(2, MathArrays.Position.HEAD, new int[]{1, 1, 1});

        // everything *MAY* change
        testUntilTrue(2, MathArrays.Position.HEAD, new int[]{1, -1, -1});
        testUntilTrue(2, MathArrays.Position.HEAD, new int[]{0, -1, -1});
        testUntilTrue(2, MathArrays.Position.HEAD, new int[]{-1, 1, -1});
        testUntilTrue(2, MathArrays.Position.HEAD, new int[]{-1, 0, -1});
        testUntilTrue(2, MathArrays.Position.HEAD, new int[]{-1, -1, 1});
        testUntilTrue(2, MathArrays.Position.HEAD, new int[]{-1, -1, 0});
    }

    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void test3HEAD() {
        testUntilTrue(3, MathArrays.Position.HEAD, new int[]{0, 0, 0});
    }

    @Test
    public void test0TAIL() {
        // everything *MAY* change
        testUntilTrue(0, MathArrays.Position.TAIL, new int[]{1, -1, -1});
        testUntilTrue(0, MathArrays.Position.TAIL, new int[]{0, -1, -1});
        testUntilTrue(0, MathArrays.Position.TAIL, new int[]{-1, 1, -1});
        testUntilTrue(0, MathArrays.Position.TAIL, new int[]{-1, 0, -1});
        testUntilTrue(0, MathArrays.Position.TAIL, new int[]{-1, -1, 1});
        testUntilTrue(0, MathArrays.Position.TAIL, new int[]{-1, -1, 0});

        // of course, only ONE change is not possible because of the shuffling property
        testAlwaysFalse(0, MathArrays.Position.TAIL, new int[]{1, 0, 0});
        testAlwaysFalse(0, MathArrays.Position.TAIL, new int[]{0, 1, 0});
        testAlwaysFalse(0, MathArrays.Position.TAIL, new int[]{0, 0, 1});

        testUntilTrue(0, MathArrays.Position.TAIL, new int[]{0, 0, 0});
        testUntilTrue(0, MathArrays.Position.TAIL, new int[]{1, 1, 1});
    }

    private static void testAlwaysTrue(int start, MathArrays.Position pos, int[] expectChange) {
        testAlways(true, start, pos, expectChange);
    }

    private static void testAlwaysFalse(int start, MathArrays.Position pos, int[] expectChange) {
        testAlways(false, start, pos, expectChange);
    }

    private static void testAlways(boolean result,
            int start, MathArrays.Position pos, int[] expectChange) {
        for (int i = 0; i < ITERATIONS; i++) {
            assertEquals(result, test(start, pos, expectChange));
        }
    }

    private static void testUntilTrue(int start, MathArrays.Position pos, int[] expectChange) {
        testUntil(true, start, pos, expectChange);
    }

    @SuppressWarnings("unused")
	private static void testUntilFalse(int start, MathArrays.Position pos, int[] expectChange) {
        testUntil(false, start, pos, expectChange);
    }

    private static void testUntil(boolean result, int start, MathArrays.Position pos, int[] expectChange) {
        while (result != test(start, pos, expectChange)) {
        }
    }

    private static boolean test(int start, MathArrays.Position pos, int[] expectChange) {
        int[] ba = new int[expectChange.length];
        init(ba);
        MathArrays.shuffle(ba, start, pos);
        checkArray(ba);
        log.debug(String.format("%d %s -> %s", start, pos, Arrays.toString(ba)));
        for (int i = 0; i < ba.length; i++) {
            if (expectChange[i] == 0 && ba[i] != i || expectChange[i] == 1 && ba[i] == i) {
                if (log.isTraceEnabled()) {
                    log.trace("failed at position: " + i);
                }
                return false;
            }
        }
        return true;
    }

    private static void init(int[] ia) {
        for (int i = 0; i < ia.length; i++) {
            ia[i] = i;
        }
    }

    private static void checkArray(int[] ba) {
        int[] flag = new int[ba.length];

        for (int i = 0; i < ba.length; i++) {
            flag[ba[i]]++;
        }
        for (int i = 0; i < flag.length; i++) {
            assertEquals(1, flag[i]);
        }
    }
}
