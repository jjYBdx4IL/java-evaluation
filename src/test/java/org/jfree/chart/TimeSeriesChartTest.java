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

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class TimeSeriesChartTest extends TestBase {

    @SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(TimeSeriesChartTest.class.getName());

    @Test
    public void testTimeSeriesChart() throws InvocationTargetException, InterruptedException {
        openWindow();

        JFreeChart chart = getTimeSeriesChart();

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        append(chartPanel);

        Screenshot.takeDesktopScreenshot("testTimeSeriesChart", true);
        waitForWindowClosingManual();
    }
}
