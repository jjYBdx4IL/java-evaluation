package tests.java.awt;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class LayoutTest {

    public static final File TEMP_DIR = Maven.getTempTestDir(LayoutTest.class);

    static class WindowGridBagLayout extends JFrame {
        private static final long serialVersionUID = 1L;

        WindowGridBagLayout() {
            super(WindowGridBagLayout.class.getSimpleName());

            GridBagConstraints c = new GridBagConstraints();
            getContentPane().setLayout(new GridBagLayout());

            JLabel label1 = new JLabel("label 1");
            c.weightx = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            getContentPane().add(label1, c);
            
            JLabel label2 = new JLabel("label 2");
            c.gridx++;
            getContentPane().add(label2, c);
            
            JButton button = new JButton("Button 1");
            c.weightx = 1f / 3;
            c.gridx = 0;
            c.gridy++;
            getContentPane().add(button, c);

            button = new JButton("Button 2");
            c.weightx = 2f / 3;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx++;
            c.gridy++;
            getContentPane().add(button, c);

            JProgressBar progress = new JProgressBar(0, 100);
            progress.setValue(33);
            progress.setStringPainted(true);
            progress.setPreferredSize(new Dimension(800, 20));
            c.weightx = 0;
            c.gridx = 0;
            c.gridy++;
            c.gridwidth = GridBagConstraints.REMAINDER;
            getContentPane().add(progress, c);

            pack();
        }
    }

    // https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
    @Test
    public void testGridBagLayout() throws InterruptedException, IOException, InvocationTargetException {
        WindowGridBagLayout win = new WindowGridBagLayout();
        win.setVisible(true);

        AWTUtils.writeToPng(win, new File(TEMP_DIR, "testGridBagLayout.png"));
        // @insert:image:testGridBagLayout.png@
        
        AWTUtils.showFrameAndWaitForCloseByUserTest(win);
    }
}
