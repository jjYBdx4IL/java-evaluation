package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.gfx.ImageUtils;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_stitching.Stitcher;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import testgroup.RequiresIsolatedVM;

/**
 *
 * @author jjYBdx4IL
 */
@Category(RequiresIsolatedVM.class)
public class StitchingTest {

    private final File TEMP_DIR = Maven.getTempTestDir(StitchingTest.class);
    private static final Logger LOG = LoggerFactory.getLogger(StitchingTest.class);

    static {
        // System.setProperty("org.bytedeco.javacpp.logger", "slf4j");
        LoadOrderDetectorTest.loadLibs("jniopencv_core", "jniopencv_stitching", "jniopencv_imgcodecs");
    }

    @Test
    public void testStitching() throws URISyntaxException, IOException {
        // split input image
        BufferedImage image = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage();
        File origFile = new File(TEMP_DIR, "original.jpg");
        ImageIO.write(image, "jpg", origFile);
        // @insert:image:original.jpg@
        final int w = image.getWidth();
        final int h = image.getHeight();
        final int overlap = w / 20;
        File outFile1 = new File(TEMP_DIR, "splitOutput1.jpg");
        ImageIO.write(ImageUtils.cropNew(image, 0, 0, w / 2 + overlap, h), "jpg", outFile1);
        // @insert:image:splitOutput1.jpg@
        File outFile2 = new File(TEMP_DIR, "splitOutput2.jpg");
        ImageIO.write(ImageUtils.cropNew(image, w / 2 - overlap, 0, w / 2 + overlap, h), "jpg", outFile2);
        // @insert:image:splitOutput2.jpg@

        MatVector imgs = new MatVector();

        Mat img = imread(outFile1.getAbsolutePath());
        assertFalse(img.empty());
        LOG.info(img.toString());
        imgs.resize(imgs.size() + 1);
        imgs.put(imgs.size() - 1, img);

        img = imread(outFile2.getAbsolutePath());
        assertFalse(img.empty());
        LOG.info(img.toString());
        imgs.resize(imgs.size() + 1);
        imgs.put(imgs.size() - 1, img);

        Mat pano = new Mat();
        Stitcher stitcher = Stitcher.create();
        int status = stitcher.stitch(imgs, pano);

        assertEquals("stitching images", Stitcher.OK, status);

        File resultImgFile = new File(TEMP_DIR, "stitched.jpg");
        assertTrue(imwrite(resultImgFile.getAbsolutePath(), pano));
        assertTrue(Files.exists(resultImgFile.toPath()));
        LOG.info("output written to " + resultImgFile.getAbsolutePath());
        // @insert:image:stitched.jpg@

        image = ImageIO.read(resultImgFile);
        assertEquals(w, image.getWidth(), 10);
        assertEquals(h, image.getHeight(), 10);
    }

}
