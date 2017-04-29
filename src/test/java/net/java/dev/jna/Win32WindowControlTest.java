package net.java.dev.jna;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class Win32WindowControlTest {

    private static final Logger LOG = LoggerFactory.getLogger(Win32WindowControlTest.class);

    @Test
    public void test() throws InterruptedException {
        Assume.assumeTrue(Surefire.isSingleTestExecution());

        while (true) {
            HWND hwnd = User32Utils.getWindowAtCursor();
            User32Utils.logHWNDInfo(hwnd);
            Thread.sleep(3000L);
        }
    }

}
