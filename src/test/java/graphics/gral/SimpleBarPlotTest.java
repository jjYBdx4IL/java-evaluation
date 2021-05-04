package graphics.gral;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.BarPlot.BarRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import org.junit.Test;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LinearGradientPaint;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * https://github.com/eseifert/gral/tree/master/gral-examples/src/main/java/de/erichseifert/gral/examples
 * 
 *
 */
public class SimpleBarPlotTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(SimpleBarPlotTest.class);

    static class SimpleBarPlot extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final Color COLOR1 = Color.red;

        public SimpleBarPlot() {
            super(new BorderLayout());
            setPreferredSize(new Dimension(800, 600));
            setBackground(Color.WHITE);            
            
            // Create example data
            @SuppressWarnings("unchecked")
            DataTable data = new DataTable(Double.class, Integer.class, String.class);
            data.add(0.1, 1, "January");
            data.add(0.2, 3, "February");
            data.add(0.3, -2, "March");
            data.add(0.4, 6, "April");
            data.add(0.5, -4, "May");
            data.add(0.6, 8, "June");
            data.add(0.7, 9, "July");
            data.add(0.8, 11, "August");

            // Create new bar plot
            BarPlot plot = new BarPlot(data);

            // Format plot
            plot.setInsets(new Insets2D.Double(40.0, 40.0, 40.0, 40.0));
            plot.setBarWidth(0.075);

            // Format bars
            BarRenderer pointRenderer = (BarRenderer) plot.getPointRenderers(data).get(0);
            pointRenderer.setColor(
                new LinearGradientPaint(0f, 0f, 0f, 1f,
                    new float[] { 0.0f, 1.0f },
                    new Color[] { COLOR1, GraphicsUtils.deriveBrighter(COLOR1) }
                )
            );
            pointRenderer.setBorderStroke(new BasicStroke(3f));
            pointRenderer.setBorderColor(
                new LinearGradientPaint(0f, 0f, 0f, 1f,
                    new float[] { 0.0f, 1.0f },
                    new Color[] { GraphicsUtils.deriveBrighter(COLOR1), COLOR1 }
                )
            );
            pointRenderer.setValueVisible(true);
            pointRenderer.setValueColumn(2);
            pointRenderer.setValueLocation(Location.CENTER);
            pointRenderer.setValueColor(GraphicsUtils.deriveDarker(COLOR1));
            pointRenderer.setValueFont(Font.decode(null).deriveFont(Font.BOLD));

            // Add plot to Swing component
            add(new InteractivePanel(plot));
        }

        public String getTitle() {
            return "Bar plot";
        }

        public String getDescription() {
            return "Bar plot with example data and color gradients";
        }
    }

    @Test
    public void test() {
        SimpleBarPlot plot = new SimpleBarPlot();
        JFrame f = new JFrame();
        f.getContentPane().add(plot);
        AWTUtils.showFrameAndWaitForCloseByUserTest(f, new File(TEMP_DIR, "test.png"));
        // @insert:image:test.png@
    }
}