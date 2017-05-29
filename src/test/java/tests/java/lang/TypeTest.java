package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
import com.github.jjYBdx4IL.test.PropertyRestorer;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("all")
public class TypeTest {

    private final PropertyRestorer propertyRestorer = PropertyRestorer.getInstance();

    @Before
    public void beforeTest() {
        propertyRestorer.restoreProps();;
    }

    @Test
    public void testCharEncodingConversion() throws UnsupportedEncodingException {
        for (byte b = -128; b < 0; b++) {
            assertEquals('�', (int)new String(new byte[]{b}, "UTF-8").charAt(0));
        }
    }

    @Test
    public void testFloatCast() {
        assertEquals(0.00000000f, 2 / 3, 0.00001);
        assertEquals(0.66666666f, (float) 2 / 3, 0.00001);
        assertEquals(0.66666666f, 2 / (float) 3, 0.00001);
    }

    @Test
    public void testFloatParseFloat() {
        assertEquals(0.1f, Float.parseFloat("0.1"), 1e-7f);
        propertyRestorer.setDefaultLocale(Locale.ROOT);
        try {
            Float.parseFloat("0,1");
            fail();
        } catch (NumberFormatException ex) {
        }
        propertyRestorer.setDefaultLocale(Locale.GERMAN);
        try {
            Float.parseFloat("0,1");
            fail();
        } catch (NumberFormatException ex) {
        }
    }

    @Test
    public void testFloatToString() {
        assertEquals("0.1", Float.toString(0.1f));
        propertyRestorer.setDefaultLocale(Locale.ROOT);
        assertEquals("0.1", Float.toString(0.1f));
        propertyRestorer.setDefaultLocale(Locale.GERMAN);
        assertEquals("0.1", Float.toString(0.1f));
    }

    @Test
    public void testDoubleLongMultiplication() {
        assertEquals("0.66", "" + (0.33d * 2L));
    }

    @Test
    public void testNativeIntDivRoundingErrors() {
        assertEquals(0, 1 / 3);
        assertEquals(0, 1 / 2);
        assertEquals(0, 2 / 3);

        assertEquals(1, 1 - 1 / 3);
        assertEquals(1, 1 - 1 / 2);
        assertEquals(1, 1 - 2 / 3);

        assertEquals(0, (-1) / 3);
        assertEquals(0, -(1 / 2));
        assertEquals(0, -2 / 3);

        assertEquals(0, -(1 / 3));
        assertEquals(0, -1 / 2);
        assertEquals(0, -2 / 3);

        assertEquals(0, (1 / -3));
        assertEquals(0, 1 / -2);
        assertEquals(0, 2 / -3);
    }

    @Test
    public void testLong() {
        Long l = new Long(0);
        assertEquals(Long.class, l.getClass());
        assertEquals("0", l.toString());
        l = Long.valueOf(3);
        assertEquals("3", l.toString());
        try {
            Long.valueOf(null);
            fail("expected exception not thrown");
        } catch (NumberFormatException ex) {
        }
    }

    @Test
    public void testReturnViaArg() {
        Long l = new Long(3L);
        assertEquals(3L, l.longValue());
        returnViaArg(l);
        assertEquals(3L, l.longValue());
    }

    @Ignore
    public void returnViaArg(Long l) {
        l++;
    }

    @Test
    public void parseLongTest() {
        String s = "os-123";
        assertEquals(123L, Long.parseLong(s.substring(s.indexOf('-') + 1)));
    }

    @Test
    public void testGiveArg() {
        tryGiveMeNull(null); // <-- works
        //tryGiveMeNull2(null); // <-- does not even compile
    }

    @Test
    public void testArgList() {
        argList("a", "b", 1, 2, 3);
    }

    @Test
    public void testLongComparison() {
        Long a = Long.valueOf(1L);
        Long b = Long.valueOf(1L);

        assertTrue(a == b);
        assertTrue(a.equals(b));
        assertTrue(a >= b);
        assertTrue(a <= b);

        a = Long.valueOf(1L);
        b = Long.valueOf(2L);
        assertTrue(a != b);
        assertTrue(!a.equals(b));
        assertFalse(a >= b);
        assertTrue(a <= b);
    }

    @Test
    public void testLongComparison2() {
        Long a = new Long(1);
        Long b = new Long(1);

        assertTrue(a != b);
        assertTrue(a.equals(b));
        assertTrue(a >= b);
        assertTrue(a <= b);

        a = new Long(1);
        b = new Long(2);
        assertTrue(a != b);
        assertTrue(!a.equals(b));
        assertFalse(a >= b);
        assertTrue(a <= b);
    }

    @Test
    public void testLongComparison3() {
        Long a = new Long(1);
        Long b = new Long(1);

        assertTrue(a == (long) b);
    }

    @Test
    public void testNullPointerLongCast() {
        Long l = null;
        try {
            long b = l;
            fail();
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testDoubleAutoConversion() {
        assertEquals(1L, (long) 1.1);
        assertEquals(1L, (long) (1L * 1.9));
        assertTrue(1.2 == 1L * 1.2);

        double value = 10 / 3;
        assertEquals(3., value, 1e-7);
        value = 10 / 3.;
        assertEquals(3.33333333333, value, 1e-7);
        
        long longValue = 1000L;
        longValue *= 1.5;
        assertEquals(1500L, longValue);
    }

    @Ignore
    public void tryGiveMeNull(Long l) {
    }

    @Ignore
    public void tryGiveMeNull2(long l) {
    }

    @Ignore
    public void argList(String s1, String s2, Integer... ids) {
        assertEquals(3, ids.length);
        assertEquals(new Integer[]{1, 2, 3}, ids);
    }

    @Test
    public void testStringReplaceAllWithRegex() {
        String s = "00:00:00:00:00:00:00:01";
        assertEquals("00:::::::1", s.replaceAll(":0+", ":"));
    }

    @Test
    public void testIntegerToOctalStringRepresentation() {
        assertEquals("101", Integer.toString(65, 8));
    }

    @Test
    public void testOctalRepresentation() {
        assertEquals(1, 01);
        assertEquals(8, 010);
        assertEquals(8, 00010);
        assertEquals(9, 011);
        assertEquals(17, 021);
        assertEquals(23, 027);
    }

    @Test
    public void testHexadecimalRepresentation() {
        assertEquals(1, 0x1);
        assertEquals(15, 0xf);
        assertEquals(16, 0x10);
        assertEquals(16, 0x0010);
        assertEquals(17, 0x11);
        assertEquals(33, 0x21);
        assertEquals(39, 0x27);
    }

    @Test
    public void testCharSpecialChars() {
        assertEquals(65533, '�');
        assertEquals("\ufffd", "�");
    }

    @Test
    public void testChar() {
        assertEquals(' ', (char) 040);
        assertEquals(' ', (char) 0x20);

        assertNotEquals((char) 0, (char) 0400);
        assertEquals((char) 256, (char) 0400);
        assertEquals((char) 2048, (char) 04000);
        assertEquals((char) 65535, (char) Character.MAX_VALUE);
        assertEquals((char) 0, (char) (Character.MAX_VALUE + 1));
        assertEquals((char) 0, (char) Character.MIN_VALUE);

        assertEquals(-256, ~((char) 0xff));
        assertEquals(-256, ~0xff);
        assertEquals(65280, ~0xff & 0xffff);
        assertEquals(65280, (char) -256);
        assertEquals(-65536, ~((char) -1));
        assertEquals((char) 65535, (char) -1);
    }

    @Test
    public void testByte() {
        assertEquals(-128, (byte) 128);
        assertEquals(-1, (byte) 255);
    }

    @Test
    public void testByteToCharCast() {
        assertEquals(0x0, (char) (byte) 0x0);
        assertEquals(0x7f, (char) (byte) 0x7f);
        assertEquals(0xff80, (char) (byte) 0x80);
        assertEquals(0xffff, (char) (byte) 0xff);
        assertEquals(0xffff, (char) (byte) 0x000000ff);
        assertEquals(0xffff, (char) (byte) 0xffffffff);
        assertEquals(0xffaa, (char) (byte) 0xffffffaa);

        assertEquals(127, (byte) 0x7f);
        assertEquals(-128, (byte) 0x80);
        assertEquals(128, ((byte) 0x80) & 0xff);
    }

    /**
     * Remember: casting is not the same as selecting bit ranges.
     *
     * Casting a byte into an int seems to have the following effects on the bit representation level:
     * <ul>
     * <li>Existing bits remain intact.
     * <li>The new (and higher) 24 bits are set to the state of the highest input bit.
     * </ul>
     */
    @Test
    public void testByteToIntCast() {
        assertEquals(0x0, (int) (byte) 0x0);
        assertEquals(0x7f, (int) (byte) 0x7f);
        assertEquals(0xffffff80, (int) (byte) 0x80);
        assertEquals(0xffffffff, (int) (byte) 0xff);
        assertEquals(0xffffffff, (int) (byte) 0x000000ff);
        assertEquals(0xffffffff, (int) (byte) 0xffffffff);
        assertEquals(0xffffffaa, (int) (byte) 0xffffffaa);

        assertEquals(0, ~((byte) 0xff));
        assertEquals(0, ~((byte) -1));
        assertEquals(0, (~(0xff)) & 0xff);
    }

    @Test
    public void testIntToByteCast() {
        assertEquals(0, (byte) 0x0);
        assertEquals(127, (byte) 0x7f);

        assertEquals(0, (byte) 0x100);
        assertEquals(0, (byte) 0xffffff00);
        assertEquals(127, (byte) 0xffffff7f);
    }

    @Test
    public void testIntToByteDecomposition() {
        Random r = new Random(0);
        final byte[] b = {0, 0, 0, 0};
        for (int i = 0; i < 1000; i++) {
            int input = r.nextInt();
            b[0] = (byte) (input >> 24);
            b[1] = (byte) (input >> 16);
            b[2] = (byte) (input >> 8);
            b[3] = (byte) (input >> 0);
            int output = (int) b[0] & 0xFF;
            output = (output << 8) | (int) b[1] & 0xFF;
            output = (output << 8) | (int) b[2] & 0xFF;
            output = (output << 8) | (int) b[3] & 0xFF;
            assertEquals(input, output);
        }
    }

    @Test
    public void testOctalParser() {
        assertEquals(17, Integer.parseInt("21", 8));
    }
}
