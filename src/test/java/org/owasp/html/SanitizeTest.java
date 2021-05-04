package org.owasp.html;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SanitizeTest {

    @Test
    public void test1() {
        String testStr = "<pre class=\"bla\">1</pre>";
        
        PolicyFactory policy = new HtmlPolicyBuilder().allowElements("pre").allowAttributes("class").onElements("pre")
            .toFactory();
        policy = policy.and(Sanitizers.FORMATTING).and(Sanitizers.LINKS).and(Sanitizers.IMAGES).and(Sanitizers.STYLES)
            .and(Sanitizers.TABLES).and(Sanitizers.BLOCKS);
        assertEquals(testStr, policy.sanitize(testStr));
    }
}
