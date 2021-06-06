package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC4;
import static org.bytedeco.opencv.global.opencv_core.CV_8SC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.CV_MAKETYPE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2BGR;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.sun.jna.Memory;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;

import testgroup.RequiresIsolatedVM;

/**
 * Goal: create an opencv image material directly from a byte buffer.
 * 
 *
 */
@Category(RequiresIsolatedVM.class)
public class DirectMatCreationTest extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(DirectMatCreationTest.class);
    public static final File TEMP_DIR = Maven.getTempTestDir(DirectMatCreationTest.class);

    int width = 480;
    int height = 480;

    @Test
    public void testJnaMemory2Mat() {
        // create some raw BGRA buffer
        Memory buffer = new Memory(width * height * 4);
        for (long i = 0; i < width * height; i++) {
            long x = i % width, y = i / width;
            buffer.setInt(i * 4, pixelFunc(x, y));
        }

        // we use the frame only to calculate values:
        Frame f = new Frame(width, height, Frame.DEPTH_UBYTE, 4);
        Mat m = new Mat(f.imageHeight, f.imageWidth, CV_MAKETYPE(f.imageDepth, f.imageChannels),
            new Pointer(buffer.getByteBuffer(0, width * height * 4).position(0)),
            f.imageStride * Math.abs(f.imageDepth) / 8);

        LOG.info("stride = " + f.imageStride);

        // remove alpha channel
        Mat m2 = new Mat();
        cvtColor(m, m2, COLOR_BGRA2BGR);

        imwrite(new File(TEMP_DIR, "testJnaMemory2Mat.png").getAbsolutePath(), m2);
        // @insert:image:testJnaMemory2Mat.png@
        f.close();
    }

    @Test
    public void testJnaMemory2Mat2() {
        // create some raw BGRA buffer
        Memory buffer = new Memory(width * height * 4);

        // we use the frame only to calculate values:
        Frame f = new Frame(width, height, Frame.DEPTH_UBYTE, 4);
        Mat m = new Mat(f.imageHeight, f.imageWidth, CV_MAKETYPE(f.imageDepth, f.imageChannels),
            new Pointer(buffer.getByteBuffer(0, width * height * 4).position(0)),
            f.imageStride * Math.abs(f.imageDepth) / 8);

        LOG.info("stride = " + f.imageStride);

        // write some image data
        ByteBuffer bb = m.createBuffer();
        for (long i = 0; i < width * height; i++) {
            long x = i % width, y = i / width;
            int pixel = pixelFunc(x, y);
            bb.put((byte) (pixel & 0xff));
            bb.put((byte) (pixel >> 8 & 0xff));
            bb.put((byte) (pixel >> 16 & 0xff));
            bb.put((byte) (pixel >> 24 & 0xff));
        }

        // remove alpha channel
        Mat m2 = new Mat();
        cvtColor(m, m2, COLOR_BGRA2BGR);

        imwrite(new File(TEMP_DIR, "testJnaMemory2Mat2.png").getAbsolutePath(), m2);
        // @insert:image:testJnaMemory2Mat2.png@
        f.close();
    }

    @Test
    public void testDirectMatPainting8SC1() {
        // signed byte, 1 channel (gray picture)
        Mat m = new Mat(width, height, CV_8SC1);
        assertEquals(1, m.channels());
        assertEquals(1, m.capacity());
        assertEquals(width, m.step());
        assertEquals(width, m.cols());
        assertEquals(height, m.rows());

        ByteBuffer bb = m.createBuffer();
        for (long i = 0; i < width * height; i++) {
            long x = i % width, y = i / width;
            // blue to gray:
            byte gray = (byte) (pixelFunc(x, y) & 0xff);
            bb.put(gray);
        }

        // negative gray values get ignored/mapped to black/0. The total
        // (Java byte -> gray value) mapping is then:
        // -128..127 -> 128x 0, 0..127
        imwrite(new File(TEMP_DIR, "testDirectMatPainting8SC1.png").getAbsolutePath(), m);
        // @insert:image:testDirectMatPainting8SC1.png@
    }

    @Test
    public void testDirectMatPainting8UC1() {
        // signed byte, 1 channel (gray picture)
        Mat m = new Mat(width, height, CV_8UC1);
        assertEquals(1, m.channels());
        assertEquals(1, m.capacity());
        assertEquals(width, m.step());
        assertEquals(width, m.cols());
        assertEquals(height, m.rows());

        ByteBuffer bb = m.createBuffer();
        for (long i = 0; i < width * height; i++) {
            long x = i % width, y = i / width;
            // blue to gray:
            byte gray = (byte) (pixelFunc(x, y) & 0xff);
            bb.put(gray);
        }

        // Java's byte is always signed, but opencv seems to treat it as
        // unsigned, ie. -1 is maximum luminosity, ie. gray value 255. The total
        // (Java byte -> gray value) mapping is then:
        // 0..127, -128..-1 -> 0..255
        imwrite(new File(TEMP_DIR, "testDirectMatPainting8UC1.png").getAbsolutePath(), m);
        // @insert:image:testDirectMatPainting8UC1.png@
    }

    @Test
    public void testConstructorValue() {
        Mat m;

        m = new Mat(width / 2, height / 2, CV_8UC1, new Scalar(0d));
        imwrite(new File(TEMP_DIR, "testConstructorValue8UC1_0.png").getAbsolutePath(), m);
        // @insert:image:testConstructorValue8UC1_0.png@

        m = new Mat(width / 2, height / 2, CV_8UC1, new Scalar(127d));
        imwrite(new File(TEMP_DIR, "testConstructorValue8UC1_127.png").getAbsolutePath(), m);
        // @insert:image:testConstructorValue8UC1_127.png@

        m = new Mat(width / 2, height / 2, CV_8UC1, new Scalar(255d));
        imwrite(new File(TEMP_DIR, "testConstructorValue8UC1_255.png").getAbsolutePath(), m);
        // @insert:image:testConstructorValue8UC1_255.png@

        m = new Mat(width / 2, height / 2, CV_8SC1, new Scalar(-128d));
        imwrite(new File(TEMP_DIR, "testConstructorValue8SC1_-128.png").getAbsolutePath(), m);
        // @insert:image:testConstructorValue8SC1_-128.png@

        m = new Mat(width / 2, height / 2, CV_8SC1, new Scalar(0d));
        imwrite(new File(TEMP_DIR, "testConstructorValue8SC1_0.png").getAbsolutePath(), m);
        // @insert:image:testConstructorValue8SC1_0.png@

        m = new Mat(width / 2, height / 2, CV_8SC1, new Scalar(127d));
        imwrite(new File(TEMP_DIR, "testConstructorValue8SC1_127.png").getAbsolutePath(), m);
        // @insert:image:testConstructorValue8SC1_127.png@

        m = new Mat(width / 2, height / 2, CV_32SC4, new Scalar(0d, 0d, 0d, 255d));
        imwrite(new File(TEMP_DIR, "testConstructorValue32SC4_black.png").getAbsolutePath(), m);
        // @insert:image:testConstructorValue32SC4_black.png@

        m = new Mat(width / 2, height / 2, CV_32SC4, new Scalar(255d, 0d, 0d, 255d));
        imwrite(new File(TEMP_DIR, "testConstructorValue32SC4_blue.png").getAbsolutePath(), m);
        // @insert:image:testConstructorValue32SC4_blue.png@

        m = new Mat(width / 2, height / 2, CV_32SC4, new Scalar(0d, 255d, 0d, 255d));
        imwrite(new File(TEMP_DIR, "testConstructorValue32SC4_green.png").getAbsolutePath(), m);
        // @insert:image:testConstructorValue32SC4_green.png@

        m = new Mat(width / 2, height / 2, CV_32SC4, new Scalar(0d, 0d, 255d, 255d));
        imwrite(new File(TEMP_DIR, "testConstructorValue32SC4_red.png").getAbsolutePath(), m);
        // @insert:image:testConstructorValue32SC4_red.png@

        m = new Mat(width / 2, height / 2, CV_32SC4, new Scalar(255d, 255d, 255d, 255d));
        imwrite(new File(TEMP_DIR, "testConstructorValue32SC4_white.png").getAbsolutePath(), m);
        // @insert:image:testConstructorValue32SC4_white.png@
    }

    private int pixelFunc(long x, long y) {
        return (int) (Math.pow((x * x + y * y) * 3, .5));
    }

}
