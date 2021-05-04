package tests.java.awt;

import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.gfx.ImageUtils;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import com.github.jjYBdx4IL.utils.junit4.Screenshot;
import ij.process.ByteProcessor;
import org.junit.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class IJTest extends InteractiveTestBase {

    private static final File TEMP_DIR = Maven.getTempTestDir(IJTest.class);

    @SuppressWarnings("unused")
    @Test
    public void test() throws InterruptedException, IOException, InvocationTargetException {
        openWindow();

        BufferedImage img = GraphicsResource.OPENIMAJ_TESTRES_SIGNTEXT.loadImage();
        img = img.getSubimage(0, img.getHeight() * 3 / 4, img.getWidth(), img.getHeight() / 4);
        appendImage(img);
        BufferedImage gimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = (Graphics2D) gimg.getGraphics();
        g.drawImage(img, 0, 0, null);
        appendImage(gimg);

        BufferedImage bimg = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_GRAY);
        Raster r = bimg.getRaster();

        ByteProcessor bp = new ByteProcessor(ImageUtils.deepCopy(gimg));
        bp.resetRoi();
        bp.dilate(1, 101);
        appendImage(bp.getBufferedImage());
        bp.erode(1, 101);
        appendImage(bp.getBufferedImage());

        Screenshot.takeDesktopScreenshot(IJTest.class.getName(), true);
        writeWindowAsPng(new File(TEMP_DIR, "IJTest.png"));
        // @insert:image:IJTest.png@
        waitForWindowClosing();

        // http://stackoverflow.com/questions/14296051/auto-crop-black-borders-from-a-scanned-image-by-making-stats-about-gray-values
        // (autoCrop example)

        // Close close = new Close(); // dilate, erode
        // Open open = new Open(); // erode, dilate

        // import sys
        // import numpy
        // import cv2 as cv
        // from PIL import Image, ImageOps, ImageDraw
        // from scipy.ndimage import morphology, label
        //
        //
        // img = ImageOps.grayscale(Image.open(sys.argv[1]))
        // im = numpy.array(img, dtype=numpy.uint8)
        //
        // im = morphology.grey_closing(img, (1, 101))
        // t, im = cv.threshold(im, 0, 1, cv.THRESH_OTSU)
        //
        // # "Clean noise".
        // im = morphology.grey_opening(im, (51, 51))
        // # Keep largest component.
        // lbl, ncc = label(im)
        // largest = 0, 0
        // for i in range(1, ncc + 1):
        // size = len(numpy.where(lbl == i)[0])
        // if size > largest[1]:
        // largest = i, size
        // for i in range(1, ncc + 1):
        // if i == largest[0]:
        // continue
        // im[lbl == i] = 0
        //
        //
        // col_sum = numpy.sum(im, axis=0)
        // row_sum = numpy.sum(im, axis=1)
        // col_mean, col_std = col_sum.mean(), col_sum.std()
        // row_mean, row_std = row_sum.mean(), row_sum.std()
        //
        // row_standard = (row_sum - row_mean) / row_std
        // col_standard = (col_sum - col_mean) / col_std
        //
        // def end_points(s, std_below_mean=-1.5):
        // i, j = 0, len(s) - 1
        // for i, rs in enumerate(s):
        // if rs > std_below_mean:
        // break
        // for j in xrange(len(s) - 1, i, -1):
        // if s[j] > std_below_mean:
        // break
        // return (i, j)
        //
        // # Bounding rectangle.
        // x1, x2 = end_points(col_standard)
        // y1, y2 = end_points(row_standard)
        //
        // #img.crop((x1, y1, x2, y2)).save(sys.argv[2]) # Crop.
        // result = img.convert('RGB')
        // draw = ImageDraw.Draw(result)
        // draw.line((x1, y1, x2, y1, x2, y2, x1, y2, x1, y1),
        // fill=(0, 255, 255), width=15)
        // result.save(sys.argv[2]) # Save with the bounding rectangle.
    }
}
