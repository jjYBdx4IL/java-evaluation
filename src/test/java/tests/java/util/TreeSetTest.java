package tests.java.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.TreeSet;

public class TreeSetTest {

    TreeSet<String> set;
    
    @Test
    public void test() {
        set = new TreeSet<>();
        set.add("b");
        set.add("a");
        assertEquals("a", set.pollFirst());
        assertEquals("b", set.pollFirst());
        
        set = new TreeSet<>();
        set.add("a");
        set.add("b");
        assertEquals("a", set.pollFirst());
        assertEquals("b", set.pollFirst());
    }
}
