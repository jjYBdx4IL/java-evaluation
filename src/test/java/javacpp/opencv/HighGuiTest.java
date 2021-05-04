package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_highgui.EVENT_LBUTTONDOWN;
import static org.bytedeco.opencv.global.opencv_highgui.imshow;
import static org.bytedeco.opencv.global.opencv_highgui.namedWindow;
import static org.bytedeco.opencv.global.opencv_highgui.setMouseCallback;
import static org.bytedeco.opencv.global.opencv_highgui.waitKey;

import com.github.jjYBdx4IL.utils.env.Surefire;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_highgui.MouseCallback;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testgroup.RequiresIsolatedVM;

@Category(RequiresIsolatedVM.class)
public class HighGuiTest extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(HighGuiTest.class);
    
    static {
        LoadOrderDetectorTest.loadLibs("jniopencv_highgui");
    }
    
    @Test
    public void testMouseCallback() {
        Mat mat = getExampleMat();
        namedWindow(HighGuiTest.class.getName());
        MouseCallback callback = new MouseCallback() {
            public void call(int event, int x, int y, int flags, Pointer userdata) {
                if (event == EVENT_LBUTTONDOWN) {
                    LOG.info(String.format("%d\t%d, %d\t%d", event, x, y, flags));
                }
            }
            
        };
        setMouseCallback(HighGuiTest.class.getName(), callback, null);
        imshow(HighGuiTest.class.getName(), mat);

        // Wait until user press some key
        waitKey(Surefire.isSingleTestExecution() ? 0 : 1000);
    }
}
