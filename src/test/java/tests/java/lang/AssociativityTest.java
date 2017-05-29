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

public class AssociativityTest {

    @SuppressWarnings("unused")
	@Test
    public void test1() {
        assertEquals(false, true ? false : (true  ? 2 : 3));
        assertEquals(false, true ? false :  true  ? 2 : 3);
        
        assertEquals(3,    (true ? false :  true) ? 2 : 3);
        assertEquals(false, true ? false :  true  ? 2 : 3);
    }
}
