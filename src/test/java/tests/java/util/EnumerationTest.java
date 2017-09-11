package tests.java.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Enumeration;
import java.util.Vector;

public class EnumerationTest {

    @Test
    public void test() {
        Vector<String> values = new Vector<>();
        values.add("one");
        values.add("two");
        String s = "";
        for (Enumeration<String> e = values.elements(); e.hasMoreElements();) {
            s += e.nextElement();
        }
        assertEquals("onetwo", s);
    }
}
