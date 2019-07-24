package tests.java.awt;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class ScreenshotTest {
    
    @Test
    public void test() {
        BufferedImage img = createDesktopScreenshot(null, 0, 0, 3, 2);
        assertEquals(2, img.getHeight());
        assertEquals(3, img.getWidth());
        assertEquals(img.getType(), BufferedImage.TYPE_INT_RGB);
    }
    
    public static BufferedImage createDesktopScreenshot() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        return createDesktopScreenshot(null, 0, 0, (int) size.getWidth(), (int) size.getHeight());
    }
    
    public static BufferedImage createDesktopScreenshot(GraphicsDevice screen, Rectangle region) {
        return createDesktopScreenshot(screen, region.x, region.y, region.width, region.height);
    }
    
    public static BufferedImage createDesktopScreenshot(GraphicsDevice screen,
            int x, int y, int w, int h) {
        try {
            final Robot objRobot;
            if (screen != null) {
                objRobot = new Robot(screen);
            } else {
                objRobot = new Robot();
            }

            return objRobot.createScreenCapture(new Rectangle(x, y, w, h));
        } catch (AWTException ex) {
            throw new RuntimeException(ex);
        }
    }

}
