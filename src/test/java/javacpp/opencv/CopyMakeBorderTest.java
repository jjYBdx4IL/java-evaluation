package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.junit.Test;

import java.io.File;

public class CopyMakeBorderTest extends Common {

    // https://docs.opencv.org/4.0.0/d2/de8/group__core__array.html#ga2ac1049c2c3dd25c2b41bffe17658a36
    private static final File TEMP_DIR = Maven.getTempTestDir(CopyMakeBorderTest.class);

    @Test
    public void test() {
        Mat mat = getExampleMat();
        imwrite(new File(TEMP_DIR, "input.jpg").getAbsolutePath(), mat);
        // @insert:image:input.jpg@

        Mat cropped = new Mat(new Size(600, 400), mat.type());
        int dtop = (mat.rows() - cropped.rows()) / 2;
        int dbottom = mat.rows() - dtop - cropped.rows();
        int dleft = (mat.cols() - cropped.cols()) / 2;
        int dright = mat.cols() - dleft - cropped.cols();
        mat.adjustROI(-dtop, -dbottom, -dleft, -dright).copyTo(cropped);
        imwrite(new File(TEMP_DIR, "cropped.jpg").getAbsolutePath(), cropped);
        // @insert:image:cropped.jpg@

        // create a blue canvas extended by 20 px to each side
        int padding = 20;
        Mat result = new Mat(cropped.rows() + 2 * padding, cropped.cols() + 2 * padding, mat.type(),
            new Scalar(255, 0, 0, 0));
        // and copy the picture into the center:
        cropped.copyTo(
            result.apply(new Rect(padding, padding, cropped.cols(), cropped.rows())));

        imwrite(new File(TEMP_DIR, "result.jpg").getAbsolutePath(), result);
        // @insert:image:result.jpg@
    }
}
