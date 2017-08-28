package tests.java.awt;

import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;

import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class ColorHsbTest {

    public static class TestFrame extends JFrame {

        public TestFrame() {
            super(ColorHsbTest.class.getSimpleName());

            setPreferredSize(new Dimension(800, 600));
            pack();
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
        }
    }

    @Test
    public void test() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        AWTUtils.showFrameAndWaitForCloseByUserTest(new TestFrame());
    }

}
