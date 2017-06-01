package org.lwjgl;

import com.github.jjYBdx4IL.utils.env.Surefire;
import static org.junit.Assume.assumeTrue;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class SimpleDrawElementsTest extends SimpleDrawElementsTestBase {

    @Test
    public void test() {
        assumeTrue(Surefire.isSingleTestExecution());
        
        run();
    }

}
