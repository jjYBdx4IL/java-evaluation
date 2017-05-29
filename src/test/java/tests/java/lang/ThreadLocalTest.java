package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.*;
import org.junit.Test;

public class ThreadLocalTest {

    @Test
    public void test1() throws InterruptedException {
        final ThreadLocal<String> threadLocalMap = new ThreadLocal<>();
        threadLocalMap.set("1");
        Thread t = new Thread() {
            @Override
            public void run() {
                threadLocalMap.set("2");
            }
        };
        t.start();
        t.join();
        assertEquals("1", threadLocalMap.get());
    }

    @Test
    public void testInitialValueNoUserDef() throws InterruptedException {
        final ThreadLocal<String> strings = new ThreadLocal<>();
        assertNull(strings.get());
    }

    @Test
    public void testInitialValueUserDef() throws InterruptedException {
        final ThreadLocal<String> strings = new ThreadLocal<String>() {
            @Override protected String initialValue() {
                 return "abc";
            }
        };
        assertNotNull(strings.get());
        assertEquals("abc", strings.get());
    }
}
