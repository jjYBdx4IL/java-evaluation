package tests.java.util.regex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.validator.routines.RegexValidator;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.junit4.RegularExpressionTestBase;
import com.google.gwt.regexp.shared.RegExp;

public class RegexTest extends RegularExpressionTestBase {

    private static final Logger log = LoggerFactory.getLogger(RegexTest.class);

    @Test
    public void testBrackets() {
        assertTrue(Pattern.compile("^[,\\s]+$").matcher("\t ,").find());
        assertFalse(Pattern.compile("^[,\\s]+$").matcher("\ta ,").find());
        assertFalse(Pattern.compile("^[.]+$").matcher("a").find());
        assertTrue(Pattern.compile("^[.]+$").matcher(".").find());
    }
    
    @Test
    public void testZeroWidthLookAheadGroupMatch() {
        assertRegexSubMatch("^(?=(.*b))", "abc", "ab");
        assertRegexSubMatch("^(?=.*(b))", "abc", "b");
        assertRegexSubMatchNG("^(?=(?<b>.*b))(?=(?<c>.*c))", "acbd", "b", "acb", "c", "ac");
    }

    @Test
    public void testZeroWidthLookAheadAssertionApacheCommons() {
        // does not work for some reason:
        //assertTrue(new RegexValidator("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])").isValid("0aA"));
        //assertTrue(new RegexValidator("a(?=b)").isValid("ab"));
        assertTrue(new RegexValidator("ab").isValid("ab"));
        assertTrue(new RegexValidator("^ab").isValid("ab"));
        assertFalse(new RegexValidator("^b").isValid("ab"));
        //assertTrue(new RegexValidator("(?=a)").isValid("ab"));
    }
    
    @Test
    public void testZeroWidthLookAheadAssertionNative() {
        assertTrue(nativeMatch("ab", "ab"));
        assertTrue(nativeMatch("^ab", "ab"));
        assertFalse(nativeMatch("^b", "ab"));
        assertTrue(nativeMatch("(?=a)", "ab"));
        assertTrue(nativeMatch("^a(?=.*b)", "ab"));
        assertTrue(nativeMatch("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])", "0aA"));
        assertFalse(nativeMatch("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])", "0abz"));
    }
    
    @Test
    public void testZeroWidthLookAheadAssertionRegExp() {
        assertTrue(regExpMatch("ab", "ab"));
        assertTrue(regExpMatch("^ab", "ab"));
        assertFalse(regExpMatch("^b", "ab"));
        assertTrue(regExpMatch("(?=a)", "ab"));
        assertTrue(regExpMatch("^a(?=.*b)", "ab"));
        assertTrue(regExpMatch("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])", "0aA"));
        assertFalse(regExpMatch("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])", "0abz"));
    }
    
    @Ignore
    public boolean regExpMatch(String regex, String test) {
        return RegExp.compile(regex).exec(test) != null;
    }

    @Ignore
    public boolean nativeMatch(String regex, String test) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(test);
        return m.find();
    }

    @Test
    public void testNativeMatch() {
        Pattern p = Pattern.compile("[^a-z]+([a-z]+)");
        Matcher m = p.matcher("a b c");
        
        assertEquals(0, m.regionStart());
        assertEquals(5, m.regionEnd());
        
        m.find();
        log.debug(m.toString());
        assertEquals(" b", m.group(0));
        assertEquals(1, m.start());
        assertEquals(3, m.end());
        
        m.find();
        log.debug(m.toString());
        assertEquals(" c", m.group(0));
        assertEquals(3, m.start());
        assertEquals(5, m.end());
    }

    @Test
    public void testNativeAppendReplacement() {
        Pattern p = Pattern.compile("[^a-z]+([a-z]+)");
        Matcher m = p.matcher("a b c");
        StringBuffer sb = new StringBuffer();
        while(m.find()) {
            m.appendReplacement(sb, m.group(0).replaceAll("[^a-z]", "").toUpperCase());
        }
        assertEquals("aBC", sb.toString());
    }

    @Test
    public void testQuant() {
        assertTrue(Pattern.compile("^\\d{3,}$").matcher("123").find());
    }

    @Test
    public void testNativeWordBreak() {
        assertFalse(Pattern.compile("\\ba").matcher("ba").find());
        assertTrue(Pattern.compile("\\ba").matcher("(a").find());
        assertTrue(Pattern.compile("\\ba").matcher("a").find());
    }

    @Test
    public void testNative1() {
        Pattern p = Pattern.compile("bytes", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher("bytes=123-");

        assertTrue(m.find());
        assertEquals("bytes", m.group());
        assertEquals(0, m.start());
    }

    @Test
    public void testNative2() {
        Pattern p = Pattern.compile("bytes", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(" bytes=123-");

        assertTrue(m.find());
        assertEquals("bytes", m.group());
        assertEquals(1, m.start());
    }

    @Test
    public void testNative3() {
        Pattern p = Pattern.compile("bytes\\s*=\\s*(\\d+)-(\\d+)?", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(" bytes=123-");

        assertTrue(m.find());

        assertEquals("bytes=123-", m.group());
        assertEquals(1, m.start());

        assertEquals("123", m.group(1));
        assertEquals(null, m.group(2));
    }

    @Test
    public void testNative4() {
        Pattern p = Pattern.compile("bytes\\s*=\\s*(\\d+)\\s*-\\s*(\\d+)?", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(" bytes=0123-456");

        assertTrue(m.find());

        assertEquals("bytes=0123-456", m.group());
        assertEquals(1, m.start());

        assertEquals("0123", m.group(1));
        assertEquals(123L, Long.parseLong(m.group(1)));
        assertEquals("456", m.group(2));
        assertEquals(456L, Long.parseLong(m.group(2)));
    }

    @Test
    public void testNativeGreedy() {
        Matcher m = Pattern.compile("a.*b").matcher("abb");
        assertTrue(m.find());
        assertEquals("abb", m.group());
    }

    @Test
    public void testNativeNonGreedy() {
        Matcher m = Pattern.compile("a.*?b").matcher("abb");
        assertTrue(m.find());
        assertEquals("ab", m.group());
    }

    @Test
    public void testNativeGroupCount() {
        Pattern p = Pattern.compile("^---START(?:a.*|b(c).*)---$");

        Matcher m = p.matcher("---STARTa---");
        assertTrue(m.find());
        assertEquals(1, m.groupCount());
        assertNull(m.group(1));

        m = p.matcher("---STARTbc---");
        assertTrue(m.find());
        assertEquals(1, m.groupCount());
        assertEquals("c", m.group(1));

    }

    @Test
    public void testMultiMatch() {
        Pattern p = Pattern.compile("^([a-z]+\\.)*$");

        Matcher m = p.matcher("a.b.c.");
        assertTrue(m.find());
        assertEquals(1, m.groupCount());
        assertEquals("c.", m.group(1));

        p = Pattern.compile("^((?:[a-z]+\\.)*)$");

        m = p.matcher("a.b.c.");
        assertTrue(m.find());
        assertEquals(1, m.groupCount());
        assertEquals("a.b.c.", m.group(1));
    }

    @Test
    public void testNamedGroups() {
        Pattern p = Pattern.compile("^(?<one>(?:[a-z]+\\.)*)(?<two>[a-z]+)$");

        Matcher m = p.matcher("a.b.c");
        assertTrue(m.find());
        assertEquals(2, m.groupCount());
        assertEquals("a.b.", m.group("one"));
        assertEquals("c", m.group("two"));
        
        p = Pattern.compile("(?:(?<a>[0-9]+)|(?<b>[^0-9]+))");

        m = p.matcher("1.2.3");
        
        assertTrue(m.find());
        assertEquals(2, m.groupCount());
        assertEquals("1", m.group("a"));
        assertNull(m.group("b"));
        
        assertTrue(m.find());
        assertEquals(2, m.groupCount());
        assertNull(m.group("a"));
        assertEquals(".", m.group("b"));
        
        assertTrue(m.find());
        assertEquals(2, m.groupCount());
        assertEquals("2", m.group("a"));
        assertNull(m.group("b"));
    }

    @Test
    public void test123() {
        Pattern p = Pattern.compile("([0-9]+|[^0-9]+)");

        Matcher m = p.matcher("1.21.3");
        
        assertTrue(m.find());
        assertEquals(1, m.groupCount());
        assertEquals("1", m.group(1));
        
        assertTrue(m.find());
        assertEquals(1, m.groupCount());
        assertEquals(".", m.group(1));
        
        assertTrue(m.find());
        assertEquals(1, m.groupCount());
        assertEquals("21", m.group(1));
    }
    
    @SuppressWarnings("unused")
	@Test(expected = PatternSyntaxException.class)
    public void testNamedGroupsSameName() {
        Pattern p = Pattern.compile("^(?<one>.a)|(?<one>.b)$");
    }

    @Test
    public void testUnicode() throws UnsupportedEncodingException {
        Pattern p = Pattern.compile("a");
        // this should not match:
        assertTrue(p.matcher(new String(new byte[]{0, 'a'}, "UTF-8")).find());
        // and neither this:
        assertTrue(p.matcher(new String(new byte[]{-128, 'a'}, "UTF-8")).find());
        p = Pattern.compile("a", Pattern.UNICODE_CHARACTER_CLASS);
        assertTrue(p.matcher(new String(new byte[]{0, 'a'}, "UTF-8")).find());
        assertTrue(p.matcher(new String(new byte[]{-128, 'a'}, "UTF-8")).find());

        assertEquals(2, new String(new byte[]{0, 'a'}, "UTF-8").length());
        System.out.println((int)new String(new byte[]{-121}, "UTF-8").charAt(0) + "\n");
    }
}
