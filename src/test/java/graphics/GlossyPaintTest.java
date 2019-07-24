package graphics;

import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.gfx.ImageUtils;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import com.github.jjYBdx4IL.utils.junit4.RDRunner;
import com.github.jjYBdx4IL.utils.klazz.ClassReloader;

@RunWith(RDRunner.class)
public class GlossyPaintTest extends InteractiveTestBase implements Runnable {

    private static final File TEMP_DIR = Maven.getTempTestDir(GlossyPaintTest.class);
    private static final Logger log = LoggerFactory.getLogger(GlossyPaintTest.class);

    public static void main(String[] args) throws InterruptedException {
        Thread t = ClassReloader.watchLoadAndRun("target/test-classes", GlossyPaintTest.class.getName());
        // File f = new
        // File("target/test-classes/"+GlossyPaintTest.class.getName().replace(File.separator,
        // "."));
        t.join();
    }

    @Test
    public void testGlossyPaint()
        throws InterruptedException, InvocationTargetException, FontFormatException, IOException {
        openWindow(true);

        // checkered background
        BufferedImage img = new BufferedImage(160, 50, BufferedImage.TYPE_INT_ARGB);
        ImageUtils.paintCheckeredBackground(img);
        appendImage(img);

        BufferedImage layer = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        WritableRaster raster = layer.getRaster();
        int[] blackPixel = new int[] { 0, 0, 0, 127 };
        for (int x = raster.getMinX(); x < raster.getMinX() + raster.getWidth(); x++) {
            for (int y = raster.getMinY(); y < raster.getMinY() + raster.getHeight(); y++) {
                raster.setPixel(x, y, blackPixel);
            }
        }

        BufferedImage img2 = ImageUtils.alphaMerge(img, layer);
        appendImage(img2);

        // layer:
        // lower half: black, 50% transparent,
        // upper half: vertical gradient, from south to north: black to 70%
        // white, transparency 50%
        BufferedImage layer2 = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        WritableRaster raster2 = layer2.getRaster();
        int[] transparentPixel = new int[] { 0, 0, 0, 0 };
        int[] pixelSouth = new int[] { 255, 255, 255, 51 };
        int[] pixelNorth = new int[] { 255, 255, 255, 153 };
        int[] pixel = new int[4];
        int yMin = raster2.getMinY();
        int yMidpoint = yMin + raster2.getHeight() / 2;
        int yMax = yMin + raster2.getHeight() - 1;
        for (int x = raster2.getMinX(); x < raster2.getMinX() + raster2.getWidth(); x++) {
            for (int y = yMax; y > yMidpoint; y--) {
                raster2.setPixel(x, y, transparentPixel);
            }
            for (int y = yMidpoint; y >= raster2.getMinY(); y--) {
                pixel[0] = (pixelSouth[0] * (y - yMin) + pixelNorth[0] * (yMidpoint - y)) / (yMidpoint - yMin);
                pixel[1] = (pixelSouth[1] * (y - yMin) + pixelNorth[1] * (yMidpoint - y)) / (yMidpoint - yMin);
                pixel[2] = (pixelSouth[2] * (y - yMin) + pixelNorth[2] * (yMidpoint - y)) / (yMidpoint - yMin);
                pixel[3] = (pixelSouth[3] * (y - yMin) + pixelNorth[3] * (yMidpoint - y)) / (yMidpoint - yMin);
                raster2.setPixel(x, y, pixel);
            }
        }

        BufferedImage img3 = ImageUtils.alphaMerge(img2, layer2);
        appendImage(img3);

        /**
         * layer: elliptic gradient: rectangle filled with light blue, having a
         * vertically scaled radial gradient alpha channel: the radial alpha
         * gradient has its center at the rectangle's bottom border's center. It
         * goes from 70% alpha to 0% alpha where 70% is at the bottom center,
         * and reaches 0% at the bottom border's left and right ends and at the
         * top border's center. We construct the alpha channel by painting a
         * white/black radial gradient and then copying a component of the color
         * into the alpha channel of the light blue rectangle.
         */
        // draw alpha in color channels
        BufferedImage layer3alpha = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Point2D center = new Point2D.Float(img.getWidth() / 2, img.getWidth() / 2);
        float radius = img.getWidth() / 2;
        float[] dist = { 0.0f, 1.0f };
        Color white70pct = new Color(255 * 70 / 100, 255 * 70 / 100, 255 * 70 / 100);
        Color[] alphaColors = { white70pct, Color.BLACK };
        Color lightBlue = new Color(160, 160, 255);
        RadialGradientPaint pAlpha = new RadialGradientPaint(center, radius, dist, alphaColors);
        Graphics2D gAlpha = (Graphics2D) layer3alpha.getGraphics();
        gAlpha.setPaint(pAlpha);
        gAlpha.scale(1.0, 2.0 * img.getHeight() / img.getWidth());
        gAlpha.fillRect(0, 0, img.getWidth(), img.getWidth() / 2);

        // copy "alpha" into alpha channel
        WritableRaster alphaRaster = layer3alpha.getRaster();
        BufferedImage layer3 = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g = (Graphics2D) layer3.getGraphics();
        g.setColor(lightBlue);
        g.fillRect(0, 0, img.getWidth(), img.getWidth());
        raster = layer3.getRaster();
        pixel = new int[4];
        int[] alphaPixel = new int[4];
        for (int x = raster.getMinX(); x < raster.getMinX() + raster.getWidth(); x++) {
            for (int y = raster.getMinY(); y < raster.getMinY() + raster.getHeight(); y++) {
                alphaPixel = alphaRaster.getPixel(x, y, alphaPixel);
                pixel = raster.getPixel(x, y, pixel);
                pixel[3] = alphaPixel[0];
                raster.setPixel(x, y, pixel);
            }
        }

        BufferedImage img4 = ImageUtils.alphaMerge(img3, layer3);
        appendImage(img4);

        writeWindowAsPng(new File(TEMP_DIR, "GlossyPaintTest.png"));
        // @insert:image:GlossyPaintTest.png@

        // waitForWindowClosing();
    }

    @Override
    public void run() {
        try {
            testGlossyPaint();
        } catch (Exception ex) {
            log.error("", ex);
        }
    }
}
