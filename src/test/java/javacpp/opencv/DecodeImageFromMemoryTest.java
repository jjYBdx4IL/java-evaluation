package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.utils.env.Maven;

import org.apache.commons.io.FileUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import testgroup.RequiresIsolatedVM;

@Category(RequiresIsolatedVM.class)
public class DecodeImageFromMemoryTest extends Common {

    private static final File TEMP_DIR = Maven.getTempTestDir(DecodeImageFromMemoryTest.class);
    
    @Test
    public void test() throws IOException {
        BufferedImage bi = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage();
        File f = new File(TEMP_DIR, "test.png");
        ImageIO.write(bi, "png", f);
        byte[] pngData = FileUtils.readFileToByteArray(f);

        // this creates a native copy of the byte[]:
        Mat m = new Mat(pngData);
        Mat m2 = imdecode(m, IMREAD_COLOR);
        imwrite(new File(TEMP_DIR, "result.png").getAbsolutePath(), m2);
        //@insert:image:result.png@
    }
}
