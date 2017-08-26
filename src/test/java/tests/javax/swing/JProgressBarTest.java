package tests.javax.swing;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.junit.Test;

import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class JProgressBarTest extends InteractiveTestBase {

    @Test
    public void test() throws InvocationTargetException, InterruptedException {
        openWindow();

        final JProgressBar bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        bar.setStringPainted(true);
        append(bar);

        for (int i = 0; i <= 100; i++) {
            final int j = i;
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    bar.setValue(j);
                }
            });
            Thread.sleep(30L);
        }

        waitForWindowClosing();
    }
}
