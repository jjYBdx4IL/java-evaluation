package tests.java.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

public class IteratorTest {

    @Test
    public void testName() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        Iterator<String> it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals("one", it.next());
        assertTrue(it.hasNext());
        assertEquals("two", it.next());
        assertFalse(it.hasNext());
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {}
    }
}
