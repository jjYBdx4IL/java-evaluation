package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_imgproc.Canny;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.bytedeco.opencv.global.opencv_imgproc.blur;
import org.bytedeco.opencv.opencv_core.Size;

import java.io.File;

import testgroup.RequiresIsolatedVM;

@Category(RequiresIsolatedVM.class)
public class EdgeDetectionTest extends Common {

    private static final File TEMP_DIR = Maven.getTempTestDir(EdgeDetectionTest.class);
    
    @Test
    public void test() {
        Mat mat = getExampleMat();
        Mat input = copyRegion(toGray(mat), 1000, 800, 600, 400);
        imwrite(new File(TEMP_DIR, "input.jpg").getAbsolutePath(), input);
        //@insert:image:input.jpg@
        
        Mat result = new Mat(input.size(), input.type());
        Canny(input, result, 10, 100);
        imwrite(new File(TEMP_DIR, "result.png").getAbsolutePath(), result);
        //@insert:image:result.png@
        
        Mat blurred = new Mat(input.size(), input.type());
        blur(input, blurred, new Size(3, 3));
        Canny(blurred, result, 10, 100);
        imwrite(new File(TEMP_DIR, "result2.png").getAbsolutePath(), result);
        //@insert:image:result2.png@
    }
    
}
