package tests.javax.swing;

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
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class JFramePaintTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(JFramePaintTest.class);
    
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
        AWTUtils.showFrameAndWaitForCloseByUserTest(frame, new File(TEMP_DIR, "test.png"));
        // @insert:image:test.png@
    }

}
