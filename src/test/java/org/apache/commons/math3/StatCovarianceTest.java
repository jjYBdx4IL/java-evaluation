/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.apache.commons.math3;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.junit.Test;

/**
*
* @author Github jjYBdx4IL Projects
*/
public class StatCovarianceTest {

	private final static double precision = 1e-10;
	
	/**
	 * Unbiased covariances are given by the formula
	 * cov(X, Y) = sum [(xi - E(X))(yi - E(Y))] / (n - 1)
	 * where E(X) is the mean of X and E(Y) is the mean of the Y values.
	 * Non-bias-corrected estimates use n in place of n - 1.
	 * Whether or not covariances are bias-corrected is determined by the optional parameter,
	 * "biasCorrected," which defaults to true.
	 * 
	 * Source:  https://commons.apache.org/proper/commons-math/userguide/stat.html#a1.7_Covariance_and_correlation
	 */
	@Test
	public void testCovariance() {
		assertEquals(1.0, new Covariance().covariance(
				new double[]{0.0, 1.0, 2.0},
				new double[]{0.0, 1.0, 2.0}
				), precision);
		assertEquals(4.0, new Covariance().covariance(
				new double[]{0.0, 2.0, -2.0},
				new double[]{0.0, 2.0, -2.0}
				), precision);
	}
	
}
