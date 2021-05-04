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

public class GetClassTest {
    @Test
    public void test1() {
        assertEquals(GetClassTest.class.getName(), getClass().getName());
        assertEquals("class "+GetClassTest.class.getName(), GetClassTest.class.toString());
    }
}
