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
 *
 */
public class EnumImplementingExampleInterfaceTest {

	public boolean someExampleMethod(ExampleInterface someEnumValue) {
		return EnumImplementingExampleInterface.ONE.equals(someEnumValue);
	}
	
	@Test
	public void test() {
		assertTrue(someExampleMethod(EnumImplementingExampleInterface.ONE));
		assertFalse(someExampleMethod(EnumImplementingExampleInterface.TWO));
		assertFalse(someExampleMethod(EnumImplementingExampleInterface2.ONE));
	}
}
