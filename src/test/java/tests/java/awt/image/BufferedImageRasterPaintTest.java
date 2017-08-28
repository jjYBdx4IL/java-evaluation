package tests.java.awt.image;

import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;

import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class BufferedImageRasterPaintTest {

    final int[] pixels = new int[800 * 600];

    static class TestFrame extends JFrame {
        public final BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);

        public TestFrame() {
            super(BufferedImageRasterPaintTest.class.getSimpleName());
            setPreferredSize(new Dimension(800, 600));
            pack();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(image, 0, 0, null);
        }
    }

    @Before
    public void before() {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }

    @Test
    public void test() {

        TestFrame frame = new TestFrame();

        for (int x = 0; x < 800; x++) {
            for (int y = 0; y < 600; y++) {
                // color (hue),color (s)aturation,(b)rightness
                // a saturation of 0 means no colors at all, ie. gray scale
                // for gray colors, use *,0,(0..1) for black to white
                Color c = Color.getHSBColor((float) x / 800, (float) y / 600, 1f);
                pixels[y * 800 + x] = c.getRGB();
            }
        }

        int[] raster = ((DataBufferInt) frame.image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixels, 0, raster, 0, raster.length);

        AWTUtils.showFrameAndWaitForCloseByUserTest(frame);
    }

}
