package jna;

import static org.bytedeco.opencv.global.opencv_core.CV_MAKETYPE;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2BGR;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32VK;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;
import org.lwjgl.system.windows.User32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.javax.sound.sampled.SineWaveSynthTest;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;

/**
 * This class is intended as a replacement for {@link java.awt.Robot} on
 * Windows. It's purpose is to control MS Windows programs without requiring
 * focus, ie. it works against background windows in such a way that regular
 * user operations are not interfered with. It does not work for minimized
 * windows.
 */
public class ExtRobot extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(ExtRobot.class);

    private int autoDelay = -1;
    private int delay = 0;
    private HWND hWnd = null;

    public ExtRobot() {
    }

    /**
     * 
     * @param windowClass
     *            the window class, required
     * @param windowTitle
     *            the window title/name, optional
     * @return false if the specified window couldn't be found
     */
    public boolean setTargetWindow(String windowClass, String windowTitle) {
        if (windowClass == null) {
            throw new IllegalArgumentException("window class param may not be null");
        }
        hWnd = lib.FindWindow(windowClass, windowTitle);
        LOG.info("target window set to: " + hWnd);
        return hWnd != null;
    }

    public void setTargetWindow(HWND hWnd) {
        this.hWnd = hWnd;
    }

    public HWND getTargetWindow() {
        return hWnd;
    }

    public synchronized Mat createScreenCapture(Rectangle rect) {
        HDC hdcWindow = lib.GetDC(hWnd);
        if (hdcWindow == null) {
            throw new RuntimeException("Windows GetLastError=" + Kernel32.INSTANCE.GetLastError());
        }
        HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);

        if (rect == null) {
            RECT bounds = new RECT();
            lib.GetClientRect(hWnd, bounds);
            rect = new Rectangle(bounds.left, bounds.top, bounds.right, bounds.bottom);
        } else {
            if (rect.width % 2 != 0 || rect.height % 2 != 0) {
                LOG.warn("screen capture region width and height values should be a multiple of 2");
            }
        }

        HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, rect.width, rect.height);

        HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
        GDI32.INSTANCE.BitBlt(hdcMemDC, 0, 0, rect.width, rect.height, hdcWindow, rect.x, rect.y, GDI32.SRCCOPY);

        GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
        GDI32.INSTANCE.DeleteDC(hdcMemDC);

        BITMAPINFO bmi = new BITMAPINFO();
        bmi.bmiHeader.biWidth = rect.width;
        bmi.bmiHeader.biHeight = -rect.height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

        Memory buffer = new Memory(rect.width * rect.height * 4);
        GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, rect.height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

        GDI32.INSTANCE.DeleteObject(hBitmap);
        lib.ReleaseDC(hWnd, hdcWindow);

        // we use the frame only to calculate values:
        Frame f = new Frame(rect.width, rect.height, Frame.DEPTH_UBYTE, 4);
        Mat m = new Mat(f.imageHeight, f.imageWidth, CV_MAKETYPE(f.imageDepth, f.imageChannels),
            new Pointer(buffer.getByteBuffer(0, rect.width * rect.height * 4).position(0)),
            f.imageStride * Math.abs(f.imageDepth) / 8);

        // remove alpha channel
        Mat m2 = new Mat();
        cvtColor(m, m2, COLOR_BGRA2BGR);

        return m;
    }

    public synchronized void delay(int ms) {
        delay = ms;
    }

    public synchronized int getAutoDelay() {
        return autoDelay;
    }

    public synchronized void key(Win32VK code) {
        keyPress(code);
        keyRelease(code);
    }

    public synchronized void keyPress(Win32VK code) {
        lib.SendMessage(hWnd, User32.WM_KEYDOWN, new WPARAM(code.code), new LPARAM(0));
        sleep();
    }

    public synchronized void keyPress(int keycode) {
        lib.SendMessage(hWnd, User32.WM_KEYDOWN, new WPARAM(Win32VK.VK_ESCAPE.code), new LPARAM(0));
        sleep();
    }

    public synchronized void keyRelease(Win32VK code) {
        lib.SendMessage(hWnd, User32.WM_KEYUP, new WPARAM(code.code), new LPARAM(0xc0000000));
        sleep();
    }

    public synchronized void keyRelease(int keycode) {
        lib.SendMessage(hWnd, User32.WM_KEYUP, new WPARAM(Win32VK.VK_ESCAPE.code), new LPARAM(0xc0000000));
        sleep();
    }

    public synchronized void mouseMove(Point p) {
        mouseMove(p.x, p.y);
    }
    
    public synchronized void mouseMove(int x, int y) {
        if (isTargetWindowActive()) {
            LOG.trace("target window active");
            RECT rect = new RECT();
            lib.GetClientRect(getTargetWindow(), rect);
            lib.SetCursorPos(rect.left + x, rect.top + y);
        } else {
            LOG.trace("target window NOT active");
            lib.SendMessage(hWnd, User32.WM_MOUSEMOVE, new WPARAM(0), new LPARAM(x + (y << 16)));
        }
        sleep();
    }

    public synchronized void mousePressLeft() {
        mousePress(User32.WM_LBUTTONDOWN);
    }
    
    public synchronized void mousePress(int buttons) {
        int msg = User32.WM_LBUTTONDOWN;
        switch (buttons) {
            case InputEvent.BUTTON2_MASK:
            case InputEvent.BUTTON2_DOWN_MASK:
                msg = User32.WM_MBUTTONDOWN;
                break;
            case InputEvent.BUTTON3_MASK:
            case InputEvent.BUTTON3_DOWN_MASK:
                msg = User32.WM_RBUTTONDOWN;
                break;
            default:
        }
        lib.SendMessage(hWnd, msg, new WPARAM(0), new LPARAM(0));
        sleep();
    }

    public synchronized void mouseReleaseLeft() {
        mouseRelease(User32.WM_LBUTTONUP);
    }
    
    public synchronized void mouseRelease(int buttons) {
        int msg = User32.WM_LBUTTONUP;
        switch (buttons) {
            case InputEvent.BUTTON2_MASK:
            case InputEvent.BUTTON2_DOWN_MASK:
                msg = User32.WM_MBUTTONUP;
                break;
            case InputEvent.BUTTON3_MASK:
            case InputEvent.BUTTON3_DOWN_MASK:
                msg = User32.WM_RBUTTONUP;
                break;
            default:
        }
        lib.SendMessage(hWnd, msg, new WPARAM(0), new LPARAM(0));
        sleep();
    }

    public synchronized void setAutoDelay(int ms) {
        this.autoDelay = ms;
    }

    /**
     * no-op, TODO
     */
    public synchronized void waitForIdle() {
    }

    private void sleep() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }

    /**
     * no-op, TODO
     */
    public void setAutoWaitForIdle(boolean b) {
    }

    public static void main(String[] args) throws Exception {
        ExtRobot bot = new ExtRobot();
        bot.delay(500);
        bot.setAutoDelay(500);
        bot.setTargetWindow("triuiScreen", null);
        bot.mouseMove(80, 202);
        bot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON3_MASK);

        SineWaveSynthTest.beep(500);
    }

    public boolean isTargetWindowActive() {
        HWND foreground = lib.GetForegroundWindow();
        if (LOG.isTraceEnabled()) {
            LOG.trace("foreground window: " + foreground);
            LOG.trace(this + " target window: " + hWnd);
            LOG.trace("target window: " + hWnd.getPointer());
        }
        return foreground.getPointer().equals(hWnd.getPointer());
    }

    public Rectangle getTargetWindowDim() {
        RECT rect = new RECT();
        lib.GetClientRect(getTargetWindow(), rect);
        return new Rectangle(0, 0, rect.right, rect.bottom);
    }
}
