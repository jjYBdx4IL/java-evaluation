package tests.java.awt.image;

import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class BufferedImageRasterPaintTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(BufferedImageRasterPaintTest.class);
    static final int W = 640;
    static final int H = 480;
    final int[] pixels = new int[W * H];

    static class TestFrame extends JFrame {
        public final BufferedImage image = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);

        public TestFrame() {
            super(BufferedImageRasterPaintTest.class.getSimpleName());
            setPreferredSize(new Dimension(W, H));
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

        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                // color (hue),color (s)aturation,(b)rightness
                // a saturation of 0 means no colors at all, ie. gray scale
                // for gray colors, use *,0,(0..1) for black to white
                Color c = Color.getHSBColor((float) x / W, (float) y / H, 1f);
                pixels[y * W + x] = c.getRGB();
            }
        }

        int[] raster = ((DataBufferInt) frame.image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixels, 0, raster, 0, raster.length);

        AWTUtils.showFrameAndWaitForCloseByUserTest(frame, new File(TEMP_DIR, "BufferedImageRasterPaintTest.png"));
        // @insert:image:BufferedImageRasterPaintTest.png@
    }

}
