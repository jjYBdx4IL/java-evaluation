/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.util;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class LinkedListTest {

    @Test
    public void testStack() {
        LinkedList<String> list = new LinkedList<>();
        list.push("1");
        list.push("2");
        assertEquals(2, list.size());
        assertEquals("2", list.pop());
        assertEquals(1, list.size());
        assertEquals("1", list.pop());
        assertEquals(0, list.size());
    }

    @Test
    public void testFIFOQueue() {
        LinkedList<String> list = new LinkedList<>();
        list.add("1");
        list.add("2");
        assertEquals(2, list.size());
        assertEquals("1", list.pop());
        assertEquals(1, list.size());
        assertEquals("2", list.pop());
        assertEquals(0, list.size());
    }
}
