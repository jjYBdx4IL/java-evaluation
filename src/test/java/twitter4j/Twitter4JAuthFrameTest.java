package twitter4j;

import org.junit.Test;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;

public class Twitter4JAuthFrameTest {

    @Test
    public void test() {
        Twitter4JAuthFrame frame = new Twitter4JAuthFrame();
        AWTUtils.showFrameAndWaitForCloseByUser(frame);
    }

}
