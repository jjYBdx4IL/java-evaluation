package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.blur;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;

import testgroup.RequiresIsolatedVM;

@Category(RequiresIsolatedVM.class)
public class BlurTest extends Common {
    
    private static final File TEMP_DIR = Maven.getTempTestDir(BlurTest.class);
    
    @Test
    public void test() {
        Mat mat = getExampleMat();
        Mat gray = copyRegion(toGray(mat), 1000, 800, 600, 400);
        imwrite(new File(TEMP_DIR, "input.jpg").getAbsolutePath(), gray);
        //@insert:image:input.jpg@
        
        Mat result = new Mat(gray.size(), gray.type());
        blur(gray, result, new Size(8, 8));
        imwrite(new File(TEMP_DIR, "result.jpg").getAbsolutePath(), result);
        //@insert:image:result.jpg@
    }
}
