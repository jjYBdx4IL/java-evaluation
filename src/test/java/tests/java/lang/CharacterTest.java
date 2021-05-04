package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.apache.commons.codec.binary.Hex;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CharacterTest {

    public static final Character CH32 = 32;
    public static final char ch32 = 32;

	@SuppressWarnings("all")
    @Test
    public void testImplicitTypeConversion() {
        assertTrue(' ' == 32);
        assertTrue(' ' == ch32);
        assertTrue(' ' == CH32);
        assertEquals(" ", CH32.toString());
    }

    @Test
    public void testCodePoint() {
        final String ue = "ü";
        final int codePt = ue.codePointAt(0);
        final byte[] ba = new byte[]{(byte)(codePt / 256), (byte)(codePt % 256)};

        assertEquals(252, codePt);
        assertEquals(ue, "\u00fc");
        assertEquals("00fc", Hex.encodeHexString(ba));
    }
}
