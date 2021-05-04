package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_core.CV_MAKETYPE;
import static org.bytedeco.opencv.global.opencv_core.mixChannels;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2BGR;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import testgroup.RequiresIsolatedVM;

/**
 * More: https://github.com/bytedeco/javacv/blob/master/samples/
 * 
 * @author jjYBdx4IL
 */
@Category(RequiresIsolatedVM.class)
public class MixChannelsTest extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(MixChannelsTest.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(MixChannelsTest.class);

    @Test
    public void test4byteAbgr2Mat() throws IOException {
        BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                // 0xAARRGGBB
                bi.setRGB(x, y, y < 50 ? 0xffff0000 : y < 75 ? 0xff00ff00 : 0x7f0000ff);
            }
        }
        ImageIO.write(bi, "png", new File(TEMP_DIR, "test4byteAbgr2Mat1.png"));
        // @insert:image:test4byteAbgr2Mat1.png@

        DataBuffer db = bi.getRaster().getDataBuffer();
        assertEquals(db.getDataType(), DataBuffer.TYPE_BYTE);
        byte[] data = ((DataBufferByte)db).getData();

        Frame f = new Frame(100, 100, Frame.DEPTH_UBYTE, 4);
        long step = f.imageStride * Math.abs(f.imageDepth) / 8;
        LOG.info("image depth = " + f.imageDepth);
        LOG.info("image channels = " + f.imageChannels);
        // https://github.com/opencv/opencv/blob/master/modules/core/include/opencv2/core/hal/interface.h
        int type = CV_MAKETYPE(f.imageDepth, f.imageChannels);
        LOG.info("step = " + step);
        LOG.info("type = " + type);
        Mat m = new Mat(f.imageHeight, f.imageWidth, type, new BytePointer(data), step);

        imwrite(new File(TEMP_DIR, "test4byteAbgr2Mat2.png").getAbsolutePath(), m);
        // @insert:image:test4byteAbgr2Mat2.png@

        Mat m2 = new Mat(m.cols(), m.rows(), m.type());
        // https://docs.opencv.org/3.1.0/d7/d1b/group__imgproc__misc.html#ga4e0972be5de079fed4e3a10e24ef5ef0
        
        int[] fromTo = new int[] {0,3,1,0,2,1,3,2};
        mixChannels(new MatVector(m), new MatVector(m2), new IntPointer(fromTo), fromTo.length/2);
        imwrite(new File(TEMP_DIR, "test4byteAbgr2Mat3.png").getAbsolutePath(), m2);
        // @insert:image:test4byteAbgr2Mat3.png@
        
        // remove alpha
        cvtColor(m2, m2, COLOR_BGRA2BGR);
        imwrite(new File(TEMP_DIR, "test4byteAbgr2Mat4.png").getAbsolutePath(), m2);
        // @insert:image:test4byteAbgr2Mat4.png@
    }
}
