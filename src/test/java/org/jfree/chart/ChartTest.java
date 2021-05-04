package org.jfree.chart;

import com.github.jjYBdx4IL.utils.env.Maven;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ChartTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(ChartTest.class);
    private static final int W = 640;
    private static final int H = 480;
    
    @Test
    public void testPieChart() throws IOException {
        DefaultPieDataset dataSet = new DefaultPieDataset();
        dataSet.setValue("Linux", 29);
        dataSet.setValue("Mac", 20);
        dataSet.setValue("Windows", 51);

        JFreeChart chart = ChartFactory.createPieChart3D("Which operating system are you using?", // chart title
                dataSet,
                true,
                true,
                false);

        ChartUtilities.saveChartAsPNG(new File(TEMP_DIR, "testPieChart.png"), chart, W, H);
        // @insert:image:testPieChart.png@
    }
    
    @Test
    public void testXyStepChart() throws IOException {
        DefaultXYDataset dataset = new DefaultXYDataset(); 
        dataset.addSeries("key", new double[][] {{0, 1, 2, 3}, {4, 5, 6, 7}});
        JFreeChart chart = ChartFactory.createXYStepChart("title", "xAxisLabel", "yAxisLabel", dataset);
        ChartUtilities.saveChartAsPNG(new File(TEMP_DIR, "testXyStepChartTest.png"), chart, W, H);
        // @insert:image:testXyStepChartTest.png@
    }
    
    @Test
    public void testBarChart() throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // value (y), dataset label (legend description), column label (x label)
        dataset.addValue(1, "a", "A");
        dataset.addValue(2, "a", "B");
        dataset.addValue(3, "b", "A");
        dataset.addValue(4, "b", "B");
        JFreeChart chart = ChartFactory.createBarChart("title", "xAxisLabel", "yAxisLabel", dataset);
        ChartUtilities.saveChartAsPNG(new File(TEMP_DIR, "testBarChart.png"), chart, W, H);
        // @insert:image:testBarChart.png@
    }
}
