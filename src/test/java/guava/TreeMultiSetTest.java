package guava;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.TreeMultiset;
import org.junit.Test;

import java.util.Iterator;

//@meta:keywords:sorted,ordered,queue,deque,list@
public class TreeMultiSetTest {

    @Test
    public void test() {
        TreeMultiset<String> tms = TreeMultiset.create();
        tms.add("a");
        tms.add("c");
        tms.add("b");
        
        // iterator returns elements in sorted order:
        Iterator<String> it = tms.iterator();
        assertEquals("a", it.next());
        assertEquals("b", it.next());
        assertEquals("c", it.next());
        
        // (poll)FirstEntry() always returns lowest element in set:
        assertEquals("a", tms.pollFirstEntry().getElement());
        assertEquals("b", tms.pollFirstEntry().getElement());
        assertEquals("c", tms.pollFirstEntry().getElement());
    }
}
