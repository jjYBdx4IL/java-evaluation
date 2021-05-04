package tests.java.lang.reflect.dynproxy;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class DebugProxyTest {

    @Test
    public void test() {
        FooImpl impl = new FooImpl();
        Foo foo = (Foo) DebugProxy.newInstance(impl);
        foo.update();
        assertEquals(1, impl.testCounter);
    }
}
