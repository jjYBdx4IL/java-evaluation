package testutils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(RDRunner2.class)
public class RDRunner2Test {

    private static final Logger LOG = LoggerFactory.getLogger(RDRunner2Test.class);
    
    @Test
    public void test1() {
        LOG.info("1");
    }

    @Test
    public void test2() {
        LOG.info("2");
    }
}
