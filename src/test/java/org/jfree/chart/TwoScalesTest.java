/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.jfree.chart;

import com.github.jjYBdx4IL.test.Screenshot;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.junit.Test;

/**
 * based on http://www.java2s.com/Code/Java/Chart/JFreeChartDualAxisDemo2.htm
 * @author Github jjYBdx4IL Projects
 */
public class TwoScalesTest extends TestBase {

    @SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(TwoScalesTest.class.getName());

    @Test
    public void testTimeSeriesChart() throws InvocationTargetException, InterruptedException {
        openWindow();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(getClass().getCanonicalName(),
      	      "Time",
      	      "Value",
      	      (XYDataset) new TimeSeriesCollection(getTimeSeries(0)),
      	      true,
      	      false,
      	      false);

        final XYPlot plot = chart.getXYPlot();
        final NumberAxis axis2 = new NumberAxis("Secondary");
        axis2.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(1, axis2);
        plot.setDataset(1, new TimeSeriesCollection(getTimeSeries(1)));
        plot.mapDatasetToRangeAxis(1, 1);
        final StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
        renderer2.setSeriesPaint(0, Color.black);
        plot.setRenderer(1, renderer2);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        append(chartPanel);

        Screenshot.takeDesktopScreenshot("testTimeSeriesChart", true);
        waitForWindowClosingManual();
    }
}
