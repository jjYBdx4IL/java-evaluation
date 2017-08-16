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

}
