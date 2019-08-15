package tests.java.util.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedDeque;

public class ConcurrentLinkedDequeTest {

    // can be used for efficient communication between threads without explicit
    // synchronization
    @Test
    public void test() {
        ConcurrentLinkedDeque<String> d = new ConcurrentLinkedDeque<>();
        assertNull(d.poll());
        d.offer("one");
        d.offer("two");
        assertEquals("one", d.peek());
        assertEquals("one", d.poll());
        assertEquals("two", d.peek());
        assertEquals("two", d.poll());
        assertNull(d.poll());
    }
}
