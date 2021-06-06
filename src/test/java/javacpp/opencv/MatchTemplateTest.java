package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_core.CV_32FC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8SC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.minMaxLoc;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.TM_CCORR_NORMED;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.matchTemplate;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.gfx.ImageUtils;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import testgroup.RequiresIsolatedVM;

/**
 * An example based on: https://github.com/bytedeco/javacv/blob/master/samples/TemplateMatching.java
 * 
 */
//@meta:keywords:image,template,search,matching,image search,image matching@
@Category(RequiresIsolatedVM.class)
public class MatchTemplateTest extends Common {

    private static final File TEMP_DIR = Maven.getTempTestDir(MatchTemplateTest.class);
    private static final Logger LOG = LoggerFactory.getLogger(MatchTemplateTest.class);
    private static final float PREC = 1e-6f;

    @Test
    public void test() throws IOException {
        BufferedImage img1 = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage();
        BufferedImage img2 = ImageUtils.cropNew(img1, 100, 100, 300, 300);
        File largePic = new File(TEMP_DIR, "large.jpg");
        File partPic = new File(TEMP_DIR, "part.jpg");
        ImageIO.write(img1, "jpg", largePic);
        ImageIO.write(img2, "jpg", partPic);

        Mat sourceColor = imread(largePic.getAbsolutePath());
        Mat template = imread(partPic.getAbsolutePath(), IMREAD_GRAYSCALE);
        
        Mat sourceGrey = new Mat(sourceColor.size(), CV_8UC1);
        cvtColor(sourceColor, sourceGrey, COLOR_BGR2GRAY);
        
        Size size = new Size(sourceGrey.cols()-template.cols()+1, sourceGrey.rows()-template.rows()+1);
        Mat result = new Mat(size, CV_32FC1);
        matchTemplate(sourceGrey, template, result, TM_CCORR_NORMED);
        
        DoublePointer minVal= new DoublePointer();
        DoublePointer maxVal= new DoublePointer();
        Point min = new Point();
        Point max = new Point();
        minMaxLoc(result, minVal, maxVal, min, max, null);
        LOG.info("best match: " + max);
        rectangle(sourceColor,new Rect(max.x(),max.y(),template.cols(),template.rows()), new Scalar (192,64,64,0), 6, 0, 0);
        
        imwrite(new File(TEMP_DIR, "result.jpg").getAbsolutePath(), sourceColor);
        //@insert:image:result.jpg@
    }
    
    @Test
    public void testMatching() {
        int width = 100;
        int height = 100;
        
        Mat m8uc1_0 = new Mat(width, height, CV_8UC1, new Scalar(0d));
        imwrite(new File(TEMP_DIR, "testMatching8UC1_0.png").getAbsolutePath(), m8uc1_0);
        // @insert:image:testMatching8UC1_0.png@
        
        Mat m8uc1_1 = new Mat(width, height, CV_8UC1, new Scalar(1d));
        imwrite(new File(TEMP_DIR, "testMatching8UC1_1.png").getAbsolutePath(), m8uc1_1);
        // @insert:image:testMatching8UC1_1.png@
        
        Mat m8uc1_127 = new Mat(width, height, CV_8UC1, new Scalar(127d));
        imwrite(new File(TEMP_DIR, "testMatching8UC1_127.png").getAbsolutePath(), m8uc1_127);
        // @insert:image:testMatching8UC1_127.png@
        
        Mat m8uc1_255 = new Mat(width, height, CV_8UC1, new Scalar(255d));
        imwrite(new File(TEMP_DIR, "testMatching8UC1_255.png").getAbsolutePath(), m8uc1_255);
        // @insert:image:testMatching8UC1_255.png@
        
        Mat m8sc1_0 = new Mat(width, height, CV_8SC1, new Scalar(0d));
        imwrite(new File(TEMP_DIR, "testMatching8SC1_0.png").getAbsolutePath(), m8sc1_0);
        // @insert:image:testMatching8SC1_0.png@
        
        assertMatchResult(m8uc1_0, m8uc1_0, 0f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_0, m8uc1_1, 0f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_0, m8uc1_127, 0f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_0, m8uc1_255, 0f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_1, m8uc1_0, 0f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_1, m8uc1_1, 1f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_1, m8uc1_127, 1f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_1, m8uc1_255, 1f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_127, m8uc1_0, 0f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_127, m8uc1_1, 1f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_127, m8uc1_127, 1f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_127, m8uc1_255, 1f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_255, m8uc1_0, 0f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_255, m8uc1_1, 1f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_255, m8uc1_127, 1f, TM_CCORR_NORMED);
        assertMatchResult(m8uc1_255, m8uc1_255, 1f, TM_CCORR_NORMED);
        
        try {
            assertMatchResult(m8sc1_0, m8sc1_0, 1f, TM_CCORR_NORMED);
            fail();
        } catch (RuntimeException ex) {
        }
    }
    
    void assertMatchResult(Mat image, Mat tpl, float expectedResult, int method) {
        Size size = new Size(1,1);
        Mat result = new Mat(size, CV_32FC1);
        matchTemplate(image, tpl, result, method);
        assertEquals(expectedResult, result.<FloatBuffer>createBuffer().get(), PREC);
    }

}
