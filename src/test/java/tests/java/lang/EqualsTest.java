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

public class EqualsTest {
    @Test
    public void test1() {
        Long nullOfTypeLong = null;
        Long validLong = 0L;
        assertTrue(nullOfTypeLong != validLong);
    }
}
