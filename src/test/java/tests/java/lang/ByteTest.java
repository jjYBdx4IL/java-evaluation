package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
// CHECKSTYLE IGNORE MagicNumber FOR NEXT 1000 LINES
public class ByteTest {

    private static final Logger LOG = LoggerFactory.getLogger(ByteTest.class);

    @Test
    public void testByteValueRange() throws InterruptedException {
        byte b = 0;
        b--;
        assertEquals(-1L, (long) b);

        b = 0;
        b -= 127;
        assertEquals(-127L, (long) b);
        b--;
        assertEquals(-128L, (long) b);
        b--;
        assertEquals(127L, (long) b);

        assertEquals((byte) 127, (byte) ((int) 127));
        assertEquals((byte) -128, (byte) ((int) 128));
        assertEquals((byte) -127, (byte) ((int) 129));

        assertEquals((byte) -1, (byte) ((int) 255));
        assertEquals((byte) 0, (byte) ((int) 256));
        assertEquals((byte) 1, (byte) ((int) 257));

        for (int i = 0; i < 1000; i++) {
            assertEquals(((i + 128) % 256) - 128, (int) ((byte) i));
        }
    }

    @Test
    public void testParse() {
        LOG.info("min radix = " + Character.MIN_RADIX);
        LOG.info("max radix = " + Character.MAX_RADIX);

        assertEquals(120, Byte.parseByte("120"));
        assertEquals(120, Byte.parseByte("120", 10));

        assertEquals(1, Byte.parseByte("1", 2));
        assertEquals(4, Byte.parseByte("100", 2));

        assertEquals(10, Byte.parseByte("a", 16));
        assertEquals(127, Byte.parseByte("7f", 16));
        try {
            Byte.parseByte("80", 16);
            fail();
        } catch(NumberFormatException ex) {
        }
    }
}
