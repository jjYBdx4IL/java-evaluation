package tests.javax.swing;

import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;

import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class JFramePaintTest {

    public static class TestFrame extends JFrame {

        public TestFrame() {
            super(JFramePaintTest.class.getSimpleName());

            setPreferredSize(new Dimension(800, 600));
            pack();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setBackground(Color.blue);
            Rectangle r = g.getClipBounds();
            g2.clearRect(r.x, r.y, r.width, r.height);
        }
    }
    
    @Before
    public void before() {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }

    @Test
    public void test() {
        TestFrame frame = new TestFrame();
        AWTUtils.showFrameAndWaitForCloseByUserTest(frame);
    }

}
