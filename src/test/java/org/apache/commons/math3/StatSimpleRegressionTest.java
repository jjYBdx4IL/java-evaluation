/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2015 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.apache.commons.math3;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class StatSimpleRegressionTest {

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(StatSimpleRegressionTest.class.getName());
	
	@Test
	public void testSimpleRegression() {
		SimpleRegression sr = new SimpleRegression(true);
		double[][] data = new double[][]{
			{0, 0},
			{1, 1},
			{2, 2},
		};
		sr.addData(data);
		RegressionResults rr = sr.regress();
		
		double[] estimates = rr.getParameterEstimates();
		assertEquals(2, estimates.length);
		assertEquals(0d, estimates[0], 1e-7);
		assertEquals(1d, estimates[1], 1e-7);
	}
	
}
