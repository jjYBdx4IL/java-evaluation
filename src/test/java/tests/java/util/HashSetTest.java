/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.util;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class HashSetTest {

    @Test
    public void test() {
        Set<String> set = new HashSet<>();
        set.add("abc");
        set.add("abc");
        assertEquals(1, set.size());
    }
}
