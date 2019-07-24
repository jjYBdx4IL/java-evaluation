package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_core.NORM_MINMAX;
import static org.bytedeco.opencv.global.opencv_core.normalize;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;

import testgroup.RequiresIsolatedVM;

@Category(RequiresIsolatedVM.class)
public class NormalizeTest extends Common {
    
    private static final File TEMP_DIR = Maven.getTempTestDir(NormalizeTest.class);
    
    @Test
    public void test() {
        Mat mat = getExampleMat();
        Mat gray = copyRegion(toGray(mat), 1000, 800, 600, 400);
        imwrite(new File(TEMP_DIR, "input.jpg").getAbsolutePath(), gray);
        //@insert:image:input.jpg@
        
        Mat result = new Mat(gray.size(), gray.type());
        normalize(gray, result, 0, 255, NORM_MINMAX, gray.type(), null);
        imwrite(new File(TEMP_DIR, "result.jpg").getAbsolutePath(), result);
        //@insert:image:result.jpg@
    }
}
