package jna;

import static org.bytedeco.opencv.global.opencv_core.CV_MAKETYPE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2BGR;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.env.Surefire;
import com.sun.jna.Memory;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import javacpp.opencv.LoadOrderDetectorTest;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testgroup.RequiresIsolatedVM;
import tests.javax.sound.sampled.SineWaveSynthTest;

import java.io.File;

/**
 * Inspired by: https://stackoverflow.com/questions/4433994/java-window-image
 * 
 *
 */
// @meta:keywords:windows,user32,gdi32@
@Category(RequiresIsolatedVM.class)
public class ScreenshotBackgroundWindowTest extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotBackgroundWindowTest.class);
    public static final File TEMP_DIR = Maven.getTempTestDir(ScreenshotBackgroundWindowTest.class);

    static {
        System.setProperty("org.bytedeco.javacpp.loadlibraries", "false");
        LoadOrderDetectorTest.loadLibs();
    }

    @Ignore
    @Test
    public void test() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());

        HWND hWnd = lib.GetForegroundWindow();

        LOG.info("switch to another window, will take screenshot of current window in 5 seconds");
        // Thread.sleep(5000);

        HDC hdcWindow = lib.GetDC(hWnd);
        HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);

        RECT bounds = new RECT();
        lib.GetClientRect(hWnd, bounds);

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);

        HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
        GDI32.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, GDI32.SRCCOPY);

        GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
        GDI32.INSTANCE.DeleteDC(hdcMemDC);

        BITMAPINFO bmi = new BITMAPINFO();
        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

        Memory buffer = new Memory(width * height * 4);
        GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

        GDI32.INSTANCE.DeleteObject(hBitmap);
        lib.ReleaseDC(hWnd, hdcWindow);

        // we use the frame only to calculate values:
        Frame f = new Frame(width, height, Frame.DEPTH_UBYTE, 4);
        Mat m = new Mat(f.imageHeight, f.imageWidth, CV_MAKETYPE(f.imageDepth, f.imageChannels),
            new Pointer(buffer.getByteBuffer(0, width * height * 4).position(0)),
            f.imageStride * Math.abs(f.imageDepth) / 8);
        
        // remove alpha channel
        Mat m2 = new Mat();
        cvtColor(m, m2, COLOR_BGRA2BGR);
        imwrite(new File(TEMP_DIR, "matresult.png").getAbsolutePath(), m2);

        SineWaveSynthTest.beep(500);
    }
}
