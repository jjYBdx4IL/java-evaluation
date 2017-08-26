/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.jfree.chart;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import com.github.jjYBdx4IL.utils.junit4.Screenshot;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class TimeSeriesChartTest extends TestBase {

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
