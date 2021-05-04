package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_core.NORM_MINMAX;
import static org.bytedeco.opencv.global.opencv_core.normalize;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.THRESH_BINARY_INV;
import static org.bytedeco.opencv.global.opencv_imgproc.blur;
import static org.bytedeco.opencv.global.opencv_imgproc.THRESH_BINARY;
import static org.bytedeco.opencv.global.opencv_imgproc.threshold;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;

import testgroup.RequiresIsolatedVM;

@Category(RequiresIsolatedVM.class)
public class ThresholdTest extends Common {
    
    private static final File TEMP_DIR = Maven.getTempTestDir(ThresholdTest.class);
    
    @Test
    public void test() {
        Mat mat = getExampleMat();
        Mat gray = copyRegion(toGray(mat), 1000, 800, 600, 400);
        Mat blurred = new Mat(gray.size(), gray.type());
        blur(gray, blurred, new Size(3, 3));        
        Mat normalized = new Mat(blurred.size(), blurred.type());
        normalize(blurred, normalized, 0, 255, NORM_MINMAX, blurred.type(), null);        
        imwrite(new File(TEMP_DIR, "input.jpg").getAbsolutePath(), normalized);
        //@insert:image:input.jpg@
        
        Mat result = new Mat(normalized.size(), normalized.type());
        threshold(normalized, result, 50, 255, THRESH_BINARY);
        imwrite(new File(TEMP_DIR, "result.jpg").getAbsolutePath(), result);
        //@insert:image:result.jpg@
        
        threshold(normalized, result, 15, 255, THRESH_BINARY);
        imwrite(new File(TEMP_DIR, "result2.jpg").getAbsolutePath(), result);
        //@insert:image:result2.jpg@
        
        threshold(normalized, result, 240, 255, THRESH_BINARY);
        imwrite(new File(TEMP_DIR, "result3.jpg").getAbsolutePath(), result);
        //@insert:image:result3.jpg@
        
        threshold(normalized, result, 50, 255, THRESH_BINARY_INV);
        imwrite(new File(TEMP_DIR, "result4.jpg").getAbsolutePath(), result);
        //@insert:image:result4.jpg@
    }
}
