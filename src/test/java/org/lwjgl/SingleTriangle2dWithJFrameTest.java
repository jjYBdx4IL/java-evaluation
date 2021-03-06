package org.lwjgl;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Using Swing/AWT with lwjgl is NOT supported and will never work on MacOS and produce random problems elsewhere.
 * DO NOT USE SWING/AWT/JavaFX with LWJGL.
 * https://github.com/LWJGL/lwjgl3/issues/149
 *
 * @author jjYBdx4IL
 */
public class SingleTriangle2dWithJFrameTest extends SingleTriangle2dTest implements ActionListener {

    private JFrameAppMainWindow frame;

    /**
     * Demo using swing GUI in separate window besides the lwjgl window.
     *
     * Closing the lwjgl window terminates the application, closing the jframe
     * does not.
     */
    @Test
    @Override
    public void test() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame = new JFrameAppMainWindow();
                Timer timer = new Timer(5000, SingleTriangle2dWithJFrameTest.this);
                timer.start();
            }
        });

        run();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.toFront();
        frame.repaint();
    }

    @SuppressWarnings("serial")
    public static class JFrameAppMainWindow extends JFrame {

        public JFrameAppMainWindow() {
            super("JFrame title");
            setPreferredSize(new Dimension(600, 800));
            pack();
            setVisible(true);
        }
    }
    
    @Override
    public void loopExit() {
        AWTUtils.close(frame);
        super.loopExit();
    }

}
