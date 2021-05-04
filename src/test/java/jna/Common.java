package jna;

import static org.junit.Assume.assumeTrue;

import com.privatejgoodies.common.base.SystemUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import org.junit.BeforeClass;

public class Common {

    static User32 lib = null;
    
    @BeforeClass
    public static void beforeClass() {
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
        lib = User32.INSTANCE;
    }

    protected WinUser.MSG waitForMessage(int timeout) {
        WinUser.MSG msg = new WinUser.MSG();

        try {
            long time = System.currentTimeMillis();
            while (true) {
                while (lib.PeekMessage(msg, null, 0, 0, 1)) {
                    if (msg.message == WinUser.WM_HOTKEY) {
                        return msg;
                    }
                }
                if (System.currentTimeMillis() - time > timeout)
                    break;

                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

}
