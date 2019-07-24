package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.Locale;
import java.util.StringTokenizer;
import static org.junit.Assert.*;
import org.junit.Test;

public class StringTest {

    @Test
    public void testStringMatches() {
        assertFalse("\n".matches(".*"));
        assertTrue("\n".matches("\\n*"));
        assertTrue("\nZZ\n\nTAG\n\n".matches("(.|[\\r\\n])*TAG(.|[\\r\\n])*"));
    }

    @Test
    public void testSubstringMethod() {
        try {
            "asd".substring(0, -1);
            fail();
        } catch(StringIndexOutOfBoundsException ex) {}
    }
    
    @Test
    public void testStringFormat() {
        assertEquals("123", String.format(Locale.ROOT, "%d", 123));
        assertEquals("123", String.format(Locale.ROOT, "%d", Long.valueOf(123)));
        assertEquals("username:d000123.somebla", String.format(Locale.ROOT, "%s:d%06d.somebla", "username", 123));
        assertEquals("12,345,678", String.format(Locale.ROOT, "%,d", 12345678));
        assertEquals("4e", String.format(Locale.ROOT, "%x", 78));
        assertEquals("07", String.format(Locale.ROOT, "%02d", 7));
        assertEquals("004e", String.format(Locale.ROOT, "%04x", 78));
        assertEquals("804E", String.format(Locale.ROOT, "%X", 78+128*256));
        assertEquals("abc", String.format(Locale.ROOT, "%s", "abc"));
        assertEquals("abc -> 123", String.format(Locale.ROOT, "%s -> %s", "abc", "123"));
        assertEquals("abc", String.format(Locale.ROOT, "%1$s", "abc"));
        assertEquals("123", String.format(Locale.ROOT, "%2$s", "abc", "123"));
        assertEquals("123  ", String.format(Locale.ROOT, "%-5d", 123));
        assertEquals("+123 ", String.format(Locale.ROOT, "%-+5d", 123));
        assertEquals("  (12)", String.format(Locale.ROOT, "%(6d", -12));
    }
    
    @Test
    public void testStringFormatFloatFormatting() {
        assertEquals("0.001", String.format(Locale.ROOT, "%.3f", 0.0005));
        assertEquals("0.000", String.format(Locale.ROOT, "%.3f", 0.0004));
    }

    @Test
    public void testStringFormatOctal() {
        assertEquals("101", String.format("%o", 65));
    }
    
    @Test
    public void testStringEqualsNull() {
        assertFalse("123".equals(null));
    }
    
    @Test
    public void testStringLowerCase() {
        assertEquals("ü", "Ü".toLowerCase(Locale.ROOT));
        assertEquals("ü", "Ü".toLowerCase());
    }
    
    @Test
    public void testStringReplaceAll() {
        assertEquals(";abc;abc;", ";;;".replaceAll(";(?=;)", ";abc"));
        assertEquals("t.t/t.a", "t.t/t.t".replaceAll("\\.t$", ".a"));
        assertEquals("t.t/t.a", "t.t/t.T".replaceAll("\\.[tT]$", ".a"));
        assertEquals("t.t/t.a", "t.t/t.T".replaceAll("\\.[^.]+$", ".a"));
        assertEquals("", "/123/456".replaceAll("/.*?$", ""));
        assertEquals("/123", "/123/456".replaceAll("/[^/]*$", ""));
    }

    /**
     * String.replace() does not use regex.
     */
    @Test
    public void testStringReplace() {
        assertEquals("/123/456", "/123/456".replace("/.*$", ""));
        assertEquals("/123/456", "/123/456".replace("/.*?$", ""));
        assertEquals("/123/456", "/123/456".replace("/[^/]*$", ""));
        assertEquals("123456", "/123/456".replace("/", ""));

        String s = "a";
        s.replace("a", "b");
        assertEquals("a", s);
    }

    @Test
    public void testStringReplaceFirst() {
        assertEquals("456", "/abc/123/456".replaceFirst(".*/", ""));
        assertEquals("", "/123/456".replaceFirst("/.*$", ""));
        assertEquals("", "/123/456".replaceFirst("/.*?$", ""));
        assertEquals("asd 123  ", "   asd 123  ".replaceFirst("^\\s+", ""));
        assertEquals("   asd 123", "   asd 123  ".replaceFirst("\\s+$", ""));

        assertEquals("image", "image/jpeg".replaceFirst("^([^/]+)/.*$", "$1"));
        assertEquals("image", "image".replaceFirst("^([^/]+)/.*$", "$1"));
    }

    /**
     * StringTokenizer should not be used any more!
     */
    @Test
    public void testStringTokenizer() {
        StringTokenizer st = new StringTokenizer("123;456;;789\n", ";\r\n");
        assertTrue(st.hasMoreTokens());
        assertEquals("123", st.nextToken());
        assertTrue(st.hasMoreTokens());
        assertEquals("456", st.nextToken());
        assertTrue(st.hasMoreTokens());
        assertEquals("789", st.nextToken());
        assertFalse(st.hasMoreTokens());
    }

    @Test
    public void testStringSplit() {
        String[] sa = "123;456;;789\n".split("[;\r\n]+");
        assertEquals(3, sa.length);
        assertEquals("123", sa[0]);
        assertEquals("456", sa[1]);
        assertEquals("789", sa[2]);

        assertEquals(0, "   ".split(" ").length);
        assertEquals(1, "a".split(" ").length);
        assertEquals(1, "a ".split(" ").length);
        assertEquals(1, "a         ".split(" ").length);
        assertEquals(2, " a".split(" ").length);
        assertEquals(3, "  a".split(" ").length);
        assertEquals(4, "   a".split(" ").length);
        assertEquals(2, "a b".split(" ").length);
        assertEquals(3, "a  b".split(" ").length);
        assertEquals(4, "a   b".split(" ").length);

        assertEquals(3, "    a   b   ".split("\\s+").length);
        assertEquals(2, "    a   b   ".trim().split("\\s+").length);
    }

    @Test
    public void testStringCompareTo() {
        assertTrue("B".compareTo("A") > 0);
        assertTrue("A".compareTo("B") < 0);
        assertTrue("A".compareTo("A") == 0);
    }
    
}
