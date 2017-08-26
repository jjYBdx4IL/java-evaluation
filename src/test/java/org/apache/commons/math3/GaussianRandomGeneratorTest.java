/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.apache.commons.math3;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.junit4.Screenshot;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class GaussianRandomGeneratorTest {

	private static final Logger LOG = LoggerFactory.getLogger(GaussianRandomGeneratorTest.class);
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
		File output = new File(Screenshot.getMavenScreenshotOutputDir(), getClass().getName()+".png");
		LOG.info("" + output);
		ChartUtilities.saveChartAsPNG(output, chart, 1024, 768);
	}
}
