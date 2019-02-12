package tests.java.awt;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import org.junit.Test;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
public class ColorHsbTest extends InteractiveTestBase {

    private static final File TEMP_DIR = Maven.getTempTestDir(ColorHsbTest.class);
    private static final CountDownLatch countdown = new CountDownLatch(1);

    public static class TestFrame extends Container {

        public TestFrame() {
            setPreferredSize(new Dimension(640, 480));
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            Graphics2D g2 = (Graphics2D) g;
            Rectangle r = g.getClipBounds();
            for (int x = 0; x < r.width; x++) {
                for (int y = 0; y < r.height; y++) {
                    // color (hue),color (s)aturation,(b)rightness
                    // a saturation of 0 means no colors at all, ie. gray scale
                    // for gray colors, use *,0,(0..1) for black to white
                    g2.setColor(Color.getHSBColor((float) x / r.width, (float) y / r.height, 1f));
                    g2.fillRect(r.x + x, r.y + y, 1, 1);
                }
            }
            countdown.countDown();
        }
    }

    @Test
    public void test() throws InterruptedException, InvocationTargetException {
        openWindow();

        append(new TestFrame());
        countdown.await(5, TimeUnit.SECONDS);
        writeWindowAsPng(new File(TEMP_DIR, "ColorHsbTest.png"));
        // @insert:image:ColorHsbTest.png@
        waitForWindowClosing();
    }

}
