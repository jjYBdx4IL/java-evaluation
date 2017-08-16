/*
 * #%L
 * evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.apache.commons.lang;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;

/**
 * @author Github jjYBdx4IL Projects
 *
 */
public class ExceptionUtilsTest {

	@Test
	public void testCauseDerivedFrom() {
		Exception e = null;
		try {
			try {
				throw new UnknownHostException();
			} catch (UnknownHostException e2) {
				throw new Exception(e2);
			}
		} catch (Exception e1) {
			e = e1;
		}
		assertNotNull(e);
		assertEquals(1, ExceptionUtils.indexOfType(e, IOException.class));
		assertEquals(-1, ExceptionUtils.indexOfType(e, IllegalArgumentException.class));
	}
}
