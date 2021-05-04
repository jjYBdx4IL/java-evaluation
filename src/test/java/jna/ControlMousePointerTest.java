package jna;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;

import org.junit.Test;

public class ControlMousePointerTest extends Common {

    @Test
    public void test() {
        assumeTrue(Surefire.isSingleTestExecution());
        
        lib.SetCursorPos(100, 100);
        // clicking can be done via message to the window
    }
}
