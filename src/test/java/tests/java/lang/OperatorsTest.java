package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class OperatorsTest {

    public static final int BIT_1 = 1;
    public static final int BIT_2 = 2;
    public static final int BIT_3 = 4;
    public static final int BIT_4 = 8;

    @Test
    public void testSingleOr() {
        assertEquals(BIT_1 + BIT_3, BIT_1 | BIT_3);
    }

    @Test
    public void testNot() {
        assertEquals(-256, ~0xff);
        assertEquals(-256, ~((int)0xff));
        assertEquals(0, ~((byte)0xff));
        assertEquals(-4092, ~0xffb);
    }

    @Test
    public void testRemoveBit() {
        assertEquals(BIT_1, (BIT_1 | BIT_3) & ~BIT_3);
    }

    @Test
    public void testPrecedence() {
        assertEquals(2, 4 * 3 / 6);
        assertEquals(2, (4 * 3) / 6);
        assertEquals(0, 4 * (3 / 6));
        assertEquals(0, 4 / 6 * 3);
        assertEquals(0, (4 / 6) * 3);
    }

    @Test
    public void testBitShift() {
        assertEquals(1, 2 >> 1);
        assertEquals(4, 2 << 1);
    }
}
