package tests.java.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

public class LinkedHashSetTest {

    @Test
    public void test() {
        LinkedHashSet<String> lhs = new LinkedHashSet<>();
        try {
            lhs.iterator().next();
            fail();
        } catch (NoSuchElementException ex) {
        }
        lhs.add("1");
        lhs.add("2");
        lhs.add("3");
        
        Iterator<String> it = lhs.iterator();
        assertEquals("1", it.next());
        assertEquals("2", it.next());
        assertEquals("3", it.next());
        
        lhs.remove("2");
        lhs.add("2");
        
        it = lhs.iterator();
        assertEquals("1", it.next());
        assertEquals("3", it.next());
        assertEquals("2", it.next());
        
        assertEquals(3, lhs.size());
        
        assertEquals("[1, 3, 2]", lhs.toString());
    }
}
