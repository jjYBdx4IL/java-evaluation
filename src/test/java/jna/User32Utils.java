package jna;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
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
        char[] windowText = new char[512];
        User32.INSTANCE.GetWindowText(hWnd, windowText, 512);
        String wText = Native.toString(windowText).trim();
        LOG.info(wText);
        
        // window position/size
        RECT rect = new RECT();
        User32.INSTANCE.GetWindowRect(hWnd, rect);
        LOG.info(rect.toString());
        
        WINDOWPLACEMENT placement = new WINDOWPLACEMENT();
        User32.INSTANCE.GetWindowPlacement(hWnd, placement);
    }

    public static HWND getWindowAtCursor() {
        WinDef.POINT getPos = new WinDef.POINT();

        User32.INSTANCE.GetCursorPos(getPos);
        LOG.trace(String.format(Locale.ROOT, "pos: %d, %d", getPos.x, getPos.y));
        long getPosL = (getPos.x & 0xFFFF) + ((getPos.y & 0xFFFF) << 32);
        HWND hwnd = IUser32.instance.WindowFromPoint(getPosL);
        
        return hwnd;
    }
}
