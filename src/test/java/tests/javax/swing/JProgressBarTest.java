package tests.javax.swing;

import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;

import org.junit.Before;
import org.junit.Test;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class JProgressBarTest extends InteractiveTestBase {

    private static final File TEMP_DIR = Maven.getTempTestDir(JProgressBarTest.class);
    
    @Before
    public void before() {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }
    
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

        writeWindowAsPng(new File(TEMP_DIR, "test.png"));
        // @insert:image:test.png@
        waitForWindowClosing();
    }
}
