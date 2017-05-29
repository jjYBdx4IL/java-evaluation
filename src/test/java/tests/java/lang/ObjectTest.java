package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ObjectTest {

    @Test
    public void testToStringMethod() {
        Object o = new Object();
        Object l = new Long(1L);
        Object pb = new java.lang.ProcessBuilder();
        assertTrue(o.toString().startsWith("java.lang.Object@"));
        assertEquals("1", l.toString());
        assertTrue(pb.toString().startsWith("java.lang.ProcessBuilder@"));
    }
}
