package jna;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

import testgroup.RequiresIsolatedVM;

/**
 * inspired by:
 * https://github.com/java-native-access/jna/blob/master/contrib/w32keyhook/com/sun/jna/contrib/demo/KeyHook.java
 * 
 *
 */
//@meta:keywords:windows,user32,tld,the long dark@
@Category(RequiresIsolatedVM.class)
public class TheLongDarkAutorunTest {

    private static final Logger LOG = LoggerFactory.getLogger(TheLongDarkAutorunTest.class);

    private static HHOOK hhk = null;
    
    final char[] buf = new char[1024];
    
    private boolean autorunState = false;
    private Robot bot = null;
    
    @Before
    public void before() throws AWTException {
        bot = new Robot();
    }
    
    @Test
    public void test() throws IOException, InterruptedException {
        assumeTrue(Surefire.isSingleTestExecution());
        
        LOG.info("press \"e\" to toggle autorun in TheLongDark");
    
        final User32 lib = User32.INSTANCE;
        HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        LowLevelKeyboardProc keyboardHook = new LowLevelKeyboardProc() {
            @Override
            public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
                if (nCode >= 0) {
                    switch (wParam.intValue()) {
                        case WinUser.WM_KEYDOWN:
                            //LOG.info("keycode: " + info.vkCode);
                            if (info.vkCode == 69) {
                                toggleAutorun();
                            }
                            break;
                        case WinUser.WM_SYSKEYDOWN:
                        case WinUser.WM_KEYUP:
                        case WinUser.WM_SYSKEYUP:
                    }
                }

                Pointer ptr = info.getPointer();
                long peer = Pointer.nativeValue(ptr);
                return lib.CallNextHookEx(hhk, nCode, wParam, new LPARAM(peer));
            }
        };
        hhk = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardHook, hMod, 0);

        int result;
        MSG msg = new MSG();
        while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
            if (result == -1) {
                LOG.info("error in get message");
                break;
            } else {
                LOG.info("got message");
                lib.TranslateMessage(msg);
                lib.DispatchMessage(msg);
            }
        }

        lib.UnhookWindowsHookEx(hhk);
    }
    
    private void toggleAutorun() {
        final User32 lib = User32.INSTANCE;
        HWND hwnd = lib.GetForegroundWindow();
        assertNotNull(hwnd);
        lib.GetWindowText(hwnd, buf, buf.length);
        String title = IdentifyActiveWindowTest.toString(buf);
        LOG.info("title = " + title);
        if ("TheLongDark".equals(title)) {
            autorunState = !autorunState;
            LOG.info("autorun = " + autorunState);
            if (autorunState) {
                bot.keyPress(KeyEvent.VK_W);
            } else {
                bot.keyRelease(KeyEvent.VK_W);
            }
        }
    }
}
