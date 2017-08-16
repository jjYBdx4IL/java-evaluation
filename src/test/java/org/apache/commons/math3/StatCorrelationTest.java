/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.apache.commons.math3;

import static java.lang.Double.NaN;
import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.junit.Test;

/**
*
* @author Github jjYBdx4IL Projects
*/
@SuppressWarnings("deprecation")
public class StatCorrelationTest {

	private final static double precision = 1e-10;
	
	/**
	 * PearsonsCorrelation computes correlations defined by the formula 
	 * cor(X, Y) = sum[(xi - E(X))(yi - E(Y))] / [(n - 1)s(X)s(Y)]
	 * where E(X) and E(Y) are means of X and Y and s(X), s(Y) are standard deviations.
	 * 
	 * Source:  https://commons.apache.org/proper/commons-math/userguide/stat.html#a1.7_Covariance_and_correlation
	 */
	@Test
	public void testPearsonsCorrelation() {
		assertEquals(1.0, new PearsonsCorrelation().correlation(
				new double[]{0.0, 1.0, 2.0},
				new double[]{0.0, 1.0, 2.0}
				), precision);
		assertEquals(1.0, new PearsonsCorrelation().correlation(
				new double[]{0.0, 1.0, 2.0},
				new double[]{0.0, 2.0, 4.0}
				), precision);
		assertEquals(1.0, new PearsonsCorrelation().correlation(
				new double[]{0.0, 2.0, -2.0},
				new double[]{0.0, 2.0, -2.0}
				), precision);
		assertEquals(-1.0, new PearsonsCorrelation().correlation(
				new double[]{0.0, 1.0, 2.0},
				new double[]{0.0, -1.0, -2.0}
				), precision);
		assertEquals(0.0, new PearsonsCorrelation().correlation(
				new double[]{0.0, 1.0, 1.0},
				new double[]{0.0, -1.0, 1.0}
				), precision);
		assertEquals(Double.NaN, new PearsonsCorrelation().correlation(
				new double[]{1.0, 1.0},
				new double[]{-1.0, 1.0}
				), precision);
		assertEquals(Double.NaN, new PearsonsCorrelation().correlation(
				new double[]{1.0, 1.0, 1.0, 1.0},
				new double[]{-1.0, 1.0, -1.0, 1.0}
				), precision);
		assertEquals(Double.NaN, new PearsonsCorrelation().correlation(
				new double[]{1.0, 1.0, 1.0, 1.0},
				new double[]{1.0, 2.0, 3.0, 4.0}
				), precision);
		assertEquals(Double.NaN, new PearsonsCorrelation().correlation(
				new double[]{1.0, 1.0, 1.0},
				new double[]{1.0, 2.0, 3.0}
				), precision);
	}
	
	@Test
	public void testPearsonsCorrelationSignificance() {
		PearsonsCorrelation correlation = new PearsonsCorrelation(new double[][]{
			{0.0, 1.0, 2.0},
			{0.0, 1.0, 2.0}
		});
		assertEquals(new double[][]{
			{NaN, NaN, NaN},
			{NaN, NaN, NaN},
			{NaN, NaN, NaN}
		}, correlation.getCorrelationStandardErrors().getData());
	}

	@Test
	public void testPearsonsCorrelationSignificance2() {
		PearsonsCorrelation correlation = new PearsonsCorrelation(new double[][]{
			{0.0, 0.0},
			{1.0, 1.0},
			{2.0, 2.0}
		});
		assertEquals(new double[][]{
			{0.0, 0.0},
			{0.0, 0.0}
		}, correlation.getCorrelationStandardErrors().getData());
	}
	
	@Test
	public void testPearsonsCorrelationSignificance3() {
		PearsonsCorrelation correlation = new PearsonsCorrelation(new double[][]{
			{0.0, 0.0},
			{1.0, 1.0},
			{2.0, 2.1}
		});
		assertEquals(new double[][]{
			{0.0, 0.02748248549646603},
			{0.02748248549646603, 0.0}
		}, correlation.getCorrelationStandardErrors().getData());
	}
	
	@Test
	public void testPearsonsCorrelationSignificance3MultipliedBoth() {
		PearsonsCorrelation correlation = new PearsonsCorrelation(new double[][]{
			{0.0, 0.0},
			{2.0, 2.0},
			{4.0, 4.2}
		});
		assertEquals(new double[][]{
			{0.0, 0.02748248549646603},
			{0.02748248549646603, 0.0}
		}, correlation.getCorrelationStandardErrors().getData());
	}
	
	@Test
	public void testPearsonsCorrelationSignificance3MultipliedSecondVarOnly() {
		PearsonsCorrelation correlation = new PearsonsCorrelation(new double[][]{
			{0.0, 0.0},
			{1.0, 2.0},
			{2.0, 4.2}
		});
		assertEquals(new double[][]{
			{0.0, 0.02748248549646603},
			{0.02748248549646603, 0.0}
		}, correlation.getCorrelationStandardErrors().getData());
	}
	
	@Test
	public void testPearsonsCorrelationSignificance3IncreasedBoth() {
		PearsonsCorrelation correlation = new PearsonsCorrelation(new double[][]{
			{1.0, 1.0},
			{2.0, 2.0},
			{3.0, 3.1}
		});
		assertEquals(new double[][]{
			{0.0, 0.02748248549646603},
			{0.02748248549646603, 0.0}
		}, correlation.getCorrelationStandardErrors().getData());
	}
	
	@Test
	public void testPearsonsCorrelationSignificance3IncreasedSecondOnly() {
		PearsonsCorrelation correlation = new PearsonsCorrelation(new double[][]{
			{0.0, 1.0},
			{1.0, 2.0},
			{2.0, 3.1}
		});
		assertEquals(new double[][]{
			{0.0, 0.02748248549646603},
			{0.02748248549646603, 0.0}
		}, correlation.getCorrelationStandardErrors().getData());
	}
}
