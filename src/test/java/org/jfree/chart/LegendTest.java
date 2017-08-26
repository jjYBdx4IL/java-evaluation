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
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;

import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.junit.Test;

import com.github.jjYBdx4IL.utils.junit4.Screenshot;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class LegendTest extends TestBase {

    @Test
    public void testManualLegendCreation() throws InvocationTargetException, InterruptedException {
        openWindow();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(getClass().getCanonicalName(),
      	      "Time",
      	      "Value",
      	      (XYDataset) new TimeSeriesCollection(getTimeSeries()),
      	      false,
      	      false,
      	      false);
        
        LegendTitle legend = new LegendTitle(chart.getPlot());
        legend.setItemFont(new Font("Arial", Font.PLAIN, 12));
        legend.setBorder(0, 0, 0, 0);
        legend.setBackgroundPaint(Color.WHITE);
        legend.setPosition(RectangleEdge.BOTTOM);

        RectangleInsets padding = new RectangleInsets(5, 5, 5, 5);
        legend.setItemLabelPadding(padding);

        chart.addLegend(legend);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        append(chartPanel);

        Screenshot.takeDesktopScreenshot("testTimeSeriesChart", true);
        waitForWindowClosingManual();
    }

    @Test
    public void testImplicitLegendCreation() throws InvocationTargetException, InterruptedException {
        openWindow();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(getClass().getCanonicalName(),
      	      "Time",
      	      "Value",
      	      (XYDataset) new TimeSeriesCollection(getTimeSeries()),
      	      true,
      	      false,
      	      false);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        append(chartPanel);

        Screenshot.takeDesktopScreenshot("testTimeSeriesChart", true);
        waitForWindowClosingManual();
    }
}
