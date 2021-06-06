package org.junit;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
import java.util.Arrays;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ArrayComparisonTest {

    @SuppressWarnings("deprecation")
    @Test
    public void test() {
        byte[] a = new byte[]{0, 1, 2};
        byte[] b = new byte[]{0, 1, 2};
        byte[] c = new byte[]{0, 1, 1};

        assertArrayEquals(a, b);
        try {
            assertArrayEquals(a, c);
            fail();
        } catch (AssertionError ex) {
        }

        assertTrue(Arrays.equals(a, b));
        assertFalse(Arrays.equals(a, c));

        assertThat(a, is(b));
        try {
            assertThat(a, is(c));
            fail();
        } catch (AssertionError ex) {
        }
    }

}
