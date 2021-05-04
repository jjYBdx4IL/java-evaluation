package org.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.contrib.java.lang.system.ClearSystemProperties;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;

/**
 * https://stefanbirkner.github.io/system-rules/download.html
 * 
 * Maven: com.github.stefanbirkner:system-rules
 * 
 * @author jjYBdx4IL
 *
 */
public class SystemRulesTest {
    @Rule
    public final ProvideSystemProperty myPropertyHasMyValue = new ProvideSystemProperty("MyProperty", "MyValue");
    @Rule
    public final ClearSystemProperties myPropertyIsCleared = new ClearSystemProperties("MyProperty2");
    
    @Test
    public void test() {
        assertNull(System.getProperty("MyProperty2"));
        assertEquals("MyValue", System.getProperty("MyProperty"));
    }
}
