/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.lang.enumeration;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ValueOfTest {

	@Test
	public void test() {
		assertEquals("1", TestEnum.ONE.toString());
		try {
			TestEnum.valueOf("1");
			fail();
		} catch(IllegalArgumentException ex) {}
		assertEquals(TestEnum.ONE, TestEnum.valueOf("ONE"));
	}
}
