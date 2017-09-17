package tests.java.awt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.math.LineFeedPacking;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

public class InternalFramesTest {

    private static final Logger LOG = LoggerFactory.getLogger(InternalFramesTest.class);

    @Before
    public void before() {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }
    
    @Test
    public void testInternalFrames() {
        JFrame outerFrame = new JFrame("outerFrame");
        final JDesktopPane desktop = new JDesktopPane();
        outerFrame.setContentPane(desktop);
        outerFrame.setPreferredSize(new Dimension(800, 600));

        desktop.add(create("internal 1", 320, 260));
        desktop.add(create("internal 2", 240, 200));
        desktop.add(create("internal 3", 740, 200));

        outerFrame.pack();

        doLayout(desktop);
        
        desktop.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                LOG.info("resized " + e);
                assertEquals(desktop, e.getComponent());
                doLayout(desktop);
            }
        }); 
        
        AWTUtils.showFrameAndWaitForCloseByUserTest(outerFrame);
    }

    JInternalFrame create(String title, int w, int h) {
        JInternalFrame intFrame = new JInternalFrame(title);
        intFrame.setResizable(true);
        intFrame.setMaximizable(true);
        intFrame.setPreferredSize(new Dimension(w, h));
        intFrame.pack();
        intFrame.setVisible(true);
        return intFrame;
    }

    private void doLayout(JDesktopPane desk) {
        JInternalFrame[] frames = desk.getAllFrames();
        int[] widths = new int[frames.length];
        int[] heights = new int[frames.length];
        for (int i = 0; i < frames.length; i++) {
            widths[i] = frames[i].getPreferredSize().width;
            heights[i] = frames[i].getPreferredSize().height;
        }
        float ratio = desk.getWidth() * 1f / desk.getHeight();
        LineFeedPacking lfp = new LineFeedPacking(widths, heights, ratio);
        long bestSolution = lfp.fit();
        float scale = lfp.getOptimalSizeReductionFactor(desk.getSize());
        LOG.info("best solution = " + bestSolution + ", scale = " + scale);
        List<Point> offsets = lfp.getLayoutOffsets();
        for (int i = 0; i < frames.length; i++) {
            frames[i].setLocation((int) (offsets.get(i).x * scale), (int) (offsets.get(i).y * scale));
            frames[i].setSize((int) (widths[i] * scale), (int) (heights[i] * scale));
        }
    }

}
