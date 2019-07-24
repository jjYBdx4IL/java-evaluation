package jna;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.sun.jna.platform.win32.WinUser;

import org.apache.commons.lang.SystemUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;

import testgroup.RequiresIsolatedVM;

/**
 * inspired by:
 * https://github.com/java-native-access/jna/blob/cc1acdac02e4d0dda93ba01bbe3a3435b8933dab/contrib/platform/test/com/sun/jna/platform/win32/User32Test.java
 * 
 *
 */
//@meta:keywords:global hotkey registration,windows@
@Category(RequiresIsolatedVM.class)
public class RegisterHotKeyTest extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterHotKeyTest.class);

    @Test
    public void test() {
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
        
        int id = 1;
        assertTrue(lib.RegisterHotKey(null, id, WinUser.MOD_CONTROL | WinUser.MOD_ALT, KeyEvent.VK_D));

        if (Surefire.isSingleTestExecution()) {
            LOG.info("press CTRL-ALT-d");
            WinUser.MSG msg = waitForMessage(10000);
            assertNotNull(msg);
            assertEquals(msg.wParam.intValue(), id);
            LOG.info("success");
        }
        
        assertTrue(lib.UnregisterHotKey(null, id));
    }

}
