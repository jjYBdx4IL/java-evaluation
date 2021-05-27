package tests.java.awt;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.io.File;

import javax.swing.JFrame;

public class HiDpiTest {

    private static final Logger LOG = LoggerFactory.getLogger(HiDpiTest.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(HiDpiTest.class);

    private static final int width = 400;
    private static final int height = 400;
    
    // https://stackoverflow.com/questions/43057457/jdk-9-high-dpi-disable-for-specific-panel
    @SuppressWarnings("serial")
    @Test
    public void testDisableHiDpiForPaint() throws Exception {
        JFrame f = new JFrame("test") {
            AffineTransform descale = null;
            double scalingX = 0d;
            double scalingY = 0d;
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                AffineTransform save = g2d.getTransform();
                LOG.info("current transform: " + save);
                if (descale == null) {
                    LOG.info("initial transform: " + save);
                    descale = new AffineTransform();
                    scalingX = save.getScaleX();
                    scalingY = save.getScaleY();
                    if (scalingX == 1d && scalingY == 1d) {
                        LOG.warn("no scaling detected on this monitor");
                    }
                    descale.setToScale(1/scalingX, 1/scalingX);
                    LOG.info("scaling = {} x {}", scalingX, scalingY);
                }
                g2d.transform(descale);
                g2d.setColor(Color.black);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawLine(0, 0, (int)(width * scalingX), (int)(height * scalingY));
                g2d.setColor(Color.red);
                g2d.drawLine(0, 0, width, height);
                g2d.setTransform(save);
            }
        };
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setPreferredSize(new Dimension(400, 400));
        AWTUtils.showFrameAndWaitForCloseByUserTest(f, new File(TEMP_DIR, "testDisableHiDpiForPaint.png"));
        // @insert:image:testDisableHiDpiForPaint.png@
    }
}
