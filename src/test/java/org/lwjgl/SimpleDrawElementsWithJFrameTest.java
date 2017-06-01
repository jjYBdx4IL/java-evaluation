package org.lwjgl;

import com.github.jjYBdx4IL.utils.env.Surefire;
import java.awt.Dimension;
import javax.swing.JFrame;
import static org.junit.Assume.assumeTrue;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class SimpleDrawElementsWithJFrameTest extends SimpleDrawElementsTestBase {

    /**
     * demo using swing GUI in separate window besides the lwjgl window.
     * 
     * Closing the lwjgl window terminates the application, closing the jframe does not.
     */
    @Test
    public void test() {
        assumeTrue(Surefire.isSingleTestExecution());
        
        new JFrameAppMainWindow();
        run();
    }
    
    public static class JFrameAppMainWindow extends JFrame {
        
        public JFrameAppMainWindow() {
            super("JFrame title");
            setPreferredSize(new Dimension(600, 800));
            pack();
            setVisible(true);
        }
    }

}
