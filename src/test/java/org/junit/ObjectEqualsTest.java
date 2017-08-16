package org.junit;

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
public class ObjectEqualsTest {

    @Test
    public void testLongEquals() {
        Long l1 = 1L;
        Long l2 = 1L;
        Object o1 = l1;
        Object o2 = l2;
        assertTrue(o1 == o2);
        assertEquals(o1, o2);
    }
}
