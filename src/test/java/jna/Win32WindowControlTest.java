package jna;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.junit.Assume;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class Win32WindowControlTest {

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
