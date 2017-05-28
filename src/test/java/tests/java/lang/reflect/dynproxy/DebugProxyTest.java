package tests.java.lang.reflect.dynproxy;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class DebugProxyTest {

    private static final Logger LOG = LoggerFactory.getLogger(DebugProxyTest.class);

    @Test
    public void test() {
        FooImpl impl = new FooImpl();
        Foo foo = (Foo) DebugProxy.newInstance(impl);
        foo.update();
        assertEquals(1, impl.testCounter);
    }
}
