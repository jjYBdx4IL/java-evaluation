package net.java.dev.jna;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.WINDOWPLACEMENT;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class User32Utils {

    private static final Logger LOG = LoggerFactory.getLogger(User32Utils.class);

    public static final int y(long i) {
        return (int) (i >> 32);
    }

    public static final int x(long i) {
        return (int) (i & 0xFFFF);
    }

    public static void logHWNDInfo(HWND hWnd) {
        // window title
        byte[] windowText = new byte[512];
        IUser32.instance.GetWindowTextA(hWnd, windowText, 512);
        String wText = Native.toString(windowText).trim();
        LOG.info(wText);
        
        // window position/size
        RECT rect = new RECT();
        IUser32.instance.GetWindowRect(hWnd, rect);
        LOG.info(rect.toString());
        
        WINDOWPLACEMENT placement = new WINDOWPLACEMENT();
        IUser32.instance.GetWindowPlacement(hWnd, placement);
    }

    public static HWND getWindowAtCursor() {
        long[] getPos = new long[1];

        IUser32.instance.GetCursorPos(getPos);
        LOG.trace(String.format(Locale.ROOT, "pos: %d, %d", User32Utils.x(getPos[0]), User32Utils.y(getPos[0])));
        HWND hwnd = IUser32.instance.WindowFromPoint(getPos[0]);
        
        return hwnd;
    }
}
