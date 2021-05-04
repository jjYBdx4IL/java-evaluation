package twitter4j;

import org.junit.Test;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Surefire;

public class Twitter4JAuthFrameTest {

    @Test
    public void test() {
        assumeTrue(Surefire.isSingleTestExecution());
        
        Twitter4JAuthFrame frame = new Twitter4JAuthFrame();
        AWTUtils.showFrameAndWaitForCloseByUser(frame);
    }

}
