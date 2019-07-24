package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;

import testgroup.RequiresIsolatedVM;

@Category(RequiresIsolatedVM.class)
public class CropTest extends Common {
    
    private static final File TEMP_DIR = Maven.getTempTestDir(CropTest.class);
    
    @Test
    public void test() {
        Mat mat = getExampleMat();
        imwrite(new File(TEMP_DIR, "input.jpg").getAbsolutePath(), mat);
        //@insert:image:input.jpg@
        
        Mat result = new Mat(new Size(600, 400), mat.type());
        int dtop = (mat.rows() - result.rows())/2;
        int dbottom = mat.rows() - dtop - result.rows();
        int dleft = (mat.cols() - result.cols())/2;
        int dright = mat.cols() - dleft - result.cols();
        mat.adjustROI(-dtop, -dbottom, -dleft, -dright).copyTo(result);
        imwrite(new File(TEMP_DIR, "result.jpg").getAbsolutePath(), result);
        //@insert:image:result.jpg@
    }
}
