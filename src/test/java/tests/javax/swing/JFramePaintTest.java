package tests.javax.swing;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Surefire;

import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class JFramePaintTest extends JFrame {

    public JFramePaintTest() {
        super(JFramePaintTest.class.getSimpleName());

        setPreferredSize(new Dimension(800, 600));
        pack();
    }

    @Test
    public void test() {
        assumeTrue(Surefire.isSingleTestExecution());

        AWTUtils.showFrameAndWaitForCloseByUser(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(Color.black);
        Rectangle r = g.getClipBounds();
        g2.clearRect(r.x, r.y, r.width, r.height);
    }
}
