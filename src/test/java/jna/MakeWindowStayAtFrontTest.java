package jna;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

import org.apache.commons.lang.SystemUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;

import testgroup.RequiresIsolatedVM;

//@meta:keywords:make window stay on top,windows,user32@
@Category(RequiresIsolatedVM.class)
public class MakeWindowStayAtFrontTest extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(MakeWindowStayAtFrontTest.class);

    boolean isForceOnTop = false;
    
    @Test
    public void test() {
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
        assumeTrue(Surefire.isSingleTestExecution());
        
        int id = 12;
        assertTrue(lib.RegisterHotKey(null, id, WinUser.MOD_ALT, KeyEvent.VK_F10));
        LOG.info("press ALT-F10 to make the current foreground window stay on top");

        while (true) {
            WinUser.MSG msg = waitForMessage(10000);
            if (msg == null || msg.wParam.intValue() != id) {
                continue;
            }
            
            LOG.info("toggle");
            isForceOnTop = !isForceOnTop;
            
            HWND hwnd = lib.GetForegroundWindow();
            assertNotNull(hwnd);
            HWND HWND_TOPMOST = new HWND(Pointer.createConstant(isForceOnTop ? -1 : -2));
            lib.SetWindowPos(hwnd, HWND_TOPMOST, 0, 0, 0, 0, WinUser.SWP_NOSIZE|WinUser.SWP_NOMOVE);
        }
    }
}
