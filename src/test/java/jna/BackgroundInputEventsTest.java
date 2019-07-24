package jna;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.sun.jna.platform.win32.Win32VK;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.lwjgl.system.windows.User32;

import testgroup.RequiresIsolatedVM;

//@meta:keywords:windows,user32@
@Category(RequiresIsolatedVM.class)
public class BackgroundInputEventsTest extends Common {

    /**
     * This actually works without moving the user's mouse pointer or changing
     * focus to the target background window, though mileage might vary
     * depending on application.
     * 
     * However, mouse interaction doesn't seem to work when the window is in the
     * foreground: the app probably instantly gets updated with the real mouse
     * position after adjusting the mouse position through the message
     * interface. However, there is a simple workaround to this: control the
     * mouse pointer directly when the app has input focus.
     * 
     * Another drawback seems to be that there is no way to send key
     * combinations like SHIFT-R using this method. Modifier keys like SHIFT,
     * ALT, CONTROL are mapped via a global keyboard state. So when key events are sent
     * to a background app, the current modifier key state on the actual keyboard
     * affects those keys. Ie. if you send an 'e' to a background app while working
     * with another foreground app and you open the file menu there using alt-f,
     * the background app will receive alt-e. However, you might be able to work
     * around that by not using any keyboard events to control your background app
     * and instead only rely on mouse input.
     * 
     * So, the only good solution to controlling a background app is to put it
     * inside a VM.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testSendMouseClickToBackgroundWindow() throws InterruptedException {
        assumeTrue(Surefire.isSingleTestExecution());

        // final long x = 76, y = 202;
        final long x = 1920 / 2, y = 1080 / 2;

        HWND hWnd = lib.FindWindow("triuiScreen", null);
        assertNotNull(hWnd);

        // simple coords specification like this might not work on multi-monitor
        // setups:
        lib.SendMessage(hWnd, User32.WM_MOUSEMOVE, new WPARAM(0), new LPARAM(x + (y << 16)));

        Thread.sleep(500);

        lib.SendMessage(hWnd, User32.WM_RBUTTONDOWN, new WPARAM(0), new LPARAM(0));

        Thread.sleep(500);

        lib.SendMessage(hWnd, User32.WM_RBUTTONUP, new WPARAM(0), new LPARAM(0));
    }

    // tool for listening to window messages:
    // https://github.com/westoncampbell/SpyPlusPlus
    @Test
    public void testSendKeyboardEventsToBackgroundWindow() throws InterruptedException {
        assumeTrue(Surefire.isSingleTestExecution());

        HWND hWnd = lib.FindWindow("triuiScreen", null);
        assertNotNull(hWnd);

        // in this case there is no interruption of foreground activity in any
        // way, too
        lib.SendMessage(hWnd, User32.WM_KEYDOWN, new WPARAM(Win32VK.VK_ESCAPE.code), new LPARAM(0));
        lib.SendMessage(hWnd, User32.WM_KEYUP, new WPARAM(Win32VK.VK_ESCAPE.code), new LPARAM(0xc0000000));
    }
}
