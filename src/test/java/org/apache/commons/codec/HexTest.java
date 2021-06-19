package org.apache.commons.codec;

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

public class HexTest {

    @Test
    public void testHexDecode() throws Exception {
        byte[] a = Hex.decodeHex("0102");
        assertEquals(2, a[1]);
        
        try {
            Hex.decodeHex("01 02");
            fail();
        } catch (DecoderException ex) {}
    }
}
