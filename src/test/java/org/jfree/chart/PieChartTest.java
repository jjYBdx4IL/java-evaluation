package org.jfree.chart;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;
import org.junit.Test;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import com.github.jjYBdx4IL.utils.junit4.Screenshot;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class PieChartTest extends InteractiveTestBase {
    private static final File TEMP_DIR = Maven.getTempTestDir(PieChartTest.class);
    
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
        writeWindowAsPng(new File(TEMP_DIR, "testPieChart.png"));
        // @insert:image:testPieChart.png@
        waitForWindowClosing();
    }
}
