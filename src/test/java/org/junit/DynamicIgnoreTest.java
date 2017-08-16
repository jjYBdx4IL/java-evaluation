package org.junit;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
import static org.junit.Assert.fail;

public class DynamicIgnoreTest {

    @Before
    public void before() {
    	// run tests on Linux only
    	Assume.assumeTrue(false);
    }

    @Test
    public void testme() {
    	fail();
    }
}
