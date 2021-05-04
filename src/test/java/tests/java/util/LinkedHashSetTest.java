package tests.java.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

public class LinkedHashSetTest {

    @Test
    public void test() {
        LinkedHashSet<String> lhm = new LinkedHashSet<>();
        try {
            lhm.iterator().next();
            fail();
        } catch (NoSuchElementException ex) {
        }
        lhm.add("one");
        assertEquals("one", lhm.iterator().next());
    }
}
