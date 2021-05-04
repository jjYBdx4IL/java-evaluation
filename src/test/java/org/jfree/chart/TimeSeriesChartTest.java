/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.jfree.chart;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.junit4.Screenshot;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class TimeSeriesChartTest extends TestBase {
    private static final File TEMP_DIR = Maven.getTempTestDir(TimeSeriesChartTest.class);

    @Test
    public void testTimeSeriesChart() throws InvocationTargetException, InterruptedException {
        openWindow();

        JFreeChart chart = getTimeSeriesChart();

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        append(chartPanel);

        Screenshot.takeDesktopScreenshot("testTimeSeriesChart", true);
        writeWindowAsPng(new File(TEMP_DIR, "testTimeSeriesChart.png"));
        // @insert:image:testTimeSeriesChart.png@
        waitForWindowClosing();
    }
}
