package tests.java.lang.reflect.dynproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class FooImpl implements Foo {

    private static final Logger LOG = LoggerFactory.getLogger(FooImpl.class);
    
    public int testCounter = 0;
    
    @Override
    public void update() {
        LOG.info("upodate()");
        testCounter++;
    }

}
