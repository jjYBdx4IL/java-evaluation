/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.jfree.chart;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.junit.Test;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.junit4.Screenshot;

/**
 * based on http://www.java2s.com/Code/Java/Chart/JFreeChartDualAxisDemo2.htm
 * @author Github jjYBdx4IL Projects
 */
public class TwoScalesTest extends TestBase {
    private static final File TEMP_DIR = Maven.getTempTestDir(TwoScalesTest.class);
    
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
        writeWindowAsPng(new File(TEMP_DIR, "testTimeSeriesChart.png"));
        // @insert:image:testTimeSeriesChart.png@
        waitForWindowClosingManual();
    }
}
