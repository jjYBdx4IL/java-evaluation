/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.jfree.chart;

import com.github.jjYBdx4IL.utils.env.Maven;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class HistogramTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(HistogramTest.class);
	private static final int NUM_VALUES = 10000;
	
	@Test
	public void test() throws IOException {
		GaussianRandomGenerator gen = new GaussianRandomGenerator(new JDKRandomGenerator(0));
		double[] values = new double[NUM_VALUES];
		for (int i=0; i<NUM_VALUES; i++) {
			values[i] = gen.nextNormalizedDouble();
		}
		HistogramDataset dataset = new HistogramDataset();
		dataset.addSeries("distribution of gaussian random generator", values, (int) Math.sqrt(values.length));
		JFreeChart chart = ChartFactory.createHistogram(
				"distribution of gaussian random generator",
				"x",
				"frequency",
				dataset,
				PlotOrientation.VERTICAL,
				false, false, false);
		ChartUtilities.saveChartAsPNG(new File(TEMP_DIR, "HistogramTest.png"), chart, 640, 480);
		// @insert:image:HistogramTest.png@
	}
}
