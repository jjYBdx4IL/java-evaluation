package net.java.dev.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.WINDOWPLACEMENT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import static com.sun.jna.win32.W32APIOptions.DEFAULT_OPTIONS;

/**
 *
 * @author jjYBdx4IL
 */
public interface IUser32 extends W32APIOptions {

    IUser32 instance = (IUser32) Native.loadLibrary("user32", IUser32.class, DEFAULT_OPTIONS);

    public interface WNDENUMPROC extends StdCallLibrary.StdCallCallback {

        boolean callback(Pointer hWnd, Pointer arg);
    }

    boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer userData);

    int GetWindowTextA(HWND hWnd, byte[] lpString, int nMaxCount);

    Pointer GetWindow(Pointer hWnd, int uCmd);
    
    boolean GetWindowRect(HWND hWnd, RECT rect);
    boolean GetWindowPlacement(HWND hWnd, WINDOWPLACEMENT placement);

    /**
     * Use {@link User32Utils#x} and {@link User32Utils#y} on lpPoint[0].
     * 
     * @param lpPoint
     * @return 
     */
    boolean GetCursorPos(long[] lpPoint); 

    HWND WindowFromPoint(long point);

}
