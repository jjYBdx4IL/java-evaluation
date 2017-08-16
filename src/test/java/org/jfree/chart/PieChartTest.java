package org.jfree.chart;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.InteractiveTestBase;
import com.github.jjYBdx4IL.test.Screenshot;

import java.lang.reflect.InvocationTargetException;
import org.apache.log4j.Logger;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class PieChartTest extends InteractiveTestBase {

    @SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(PieChartTest.class.getName());

    @Test
    public void testPieChart() throws InvocationTargetException, InterruptedException {
        openWindow();

        DefaultPieDataset dataSet = new DefaultPieDataset();
        dataSet.setValue("Linux", 29);
        dataSet.setValue("Mac", 20);
        dataSet.setValue("Windows", 51);

        JFreeChart chart = ChartFactory.createPieChart3D("Which operating system are you using?", // chart title
                dataSet,
                true,
                true,
                false);

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        append(chartPanel);

        Screenshot.takeDesktopScreenshot("testPieChart", true);
        waitForWindowClosingManual();
    }
}
