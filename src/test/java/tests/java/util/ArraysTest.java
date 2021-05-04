/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.util;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ArraysTest {

    @Test
    public void test() {
        int[][] testArr = new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        System.out.println(Arrays.deepToString(testArr));
    }

    @Test
    public void testArraysEquals() {
        byte[] a01 = new byte[] {0, 1};
        byte[] b01 = new byte[] {0, 1};
        byte[] c11 = new byte[] {1, 1};
        byte[] d111 = new byte[] {1, 1, 1};
        
        assertFalse(a01.equals(b01));
        
        assertTrue(Arrays.equals(a01, b01));
        assertTrue(!Arrays.equals(a01, c11));
        assertTrue(!Arrays.equals(c11, d111));
    }
    
}
