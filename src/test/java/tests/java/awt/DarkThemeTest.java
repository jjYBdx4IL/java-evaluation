package tests.java.awt;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;

import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

public class DarkThemeTest {

    public static final File TEMP_DIR = Maven.getTempTestDir(DarkThemeTest.class);

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

    // https://stackoverflow.com/questions/36128291/how-to-make-a-swing-application-have-dark-nimbus-theme-netbeans
    @Test
    public void testDarkTheme() throws Exception {
        UIManager.put("control", new Color(128, 128, 128));
        UIManager.put("info", new Color(128, 128, 128));
        UIManager.put("nimbusBase", new Color(18, 30, 49));
        UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
        UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
        UIManager.put("nimbusFocus", new Color(115, 164, 209));
        UIManager.put("nimbusGreen", new Color(176, 179, 50));
        UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
        UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
        UIManager.put("nimbusOrange", new Color(191, 98, 4));
        UIManager.put("nimbusRed", new Color(169, 46, 34));
        UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
        UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
        UIManager.put("text", new Color(230, 230, 230));
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }

        WindowGridBagLayout win = new WindowGridBagLayout();
        win.setVisible(true);

        AWTUtils.writeToPng(win, new File(TEMP_DIR, "testDarkTheme.png"));
        // @insert:image:testDarkTheme.png@

        AWTUtils.showFrameAndWaitForCloseByUserTest(win);
    }
}
