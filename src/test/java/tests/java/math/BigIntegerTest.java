/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.math;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class BigIntegerTest {

    @Test
    public void testByteArrayInput() {
        assertEquals("0",         new BigInteger(new byte[]{ 0    }).toString(2));
        assertEquals("-1",        new BigInteger(new byte[]{-1    }).toString(2));
        assertEquals("1",         new BigInteger(new byte[]{ 0,  1}).toString(2));
        assertEquals("100",       new BigInteger(new byte[]{ 1,  0}).toString(16));
        assertEquals("100000000", new BigInteger(new byte[]{ 1,  0}).toString(2));
        assertEquals("111111111", new BigInteger(new byte[]{ 1, -1}).toString(2));
        assertEquals("1ff",       new BigInteger(new byte[]{ 1, -1}).toString(16));
    }
}
