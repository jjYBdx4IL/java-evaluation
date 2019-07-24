package jna;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testgroup.RequiresIsolatedVM;

import java.awt.event.KeyEvent;

//@meta:keywords:windows,user32@
@Category(RequiresIsolatedVM.class)
public class IdentifyActiveWindowTest extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(IdentifyActiveWindowTest.class);

    boolean isEnabled = false;

    @Test
    public void test() {
        assumeTrue(Surefire.isSingleTestExecution());

        int id = 14;
        assertTrue(lib.RegisterHotKey(null, id, 0, KeyEvent.VK_E));
        LOG.info("press \"e\" to dump info about the active window");

        char[] buf = new char[1024];
        while (true) {
            WinUser.MSG msg = waitForMessage(10000);
            if (msg == null || msg.wParam.intValue() != id) {
                continue;
            }

            LOG.info("toggle");
            isEnabled = !isEnabled;

            HWND hwnd = lib.GetForegroundWindow();
            assertNotNull(hwnd);
            lib.GetWindowModuleFileName(hwnd, buf, buf.length);
            LOG.info("module file name: " + toString(buf));
            
            lib.GetWindowText(hwnd, buf, buf.length);
            String title = toString(buf);
            LOG.info("title: " + title);
            
            lib.GetClassName(hwnd, buf, buf.length);
            String className = toString(buf);
            LOG.info("class: " + className);
            
            assertEquals(hwnd, lib.FindWindow(className, title));
            assertEquals(hwnd, lib.FindWindow(className, null));
        }
    }
    
    public static String toString(char[] chars) {
        String buf = new String(chars);
        return buf.substring(0, buf.indexOf(0));
    }
}
