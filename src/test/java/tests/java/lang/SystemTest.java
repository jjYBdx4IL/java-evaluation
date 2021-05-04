package tests.java.lang;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SystemTest {

    private static final Logger LOG = LoggerFactory.getLogger(SystemTest.class);
    
    @Test
    public void testErrPrint() {
        byte b = 0x31; // "1"
        int c = ((int)b) & 0xff;
        System.err.println((char)c);
    }
    
    @Test
    public void testProps() {
        Properties p = System.getProperties();
        p.list(System.out);
        assertNotNull(p.getProperty("user.dir"));
        LOG.info("cwd: " + p.getProperty("user.dir"));
        assertNotNull(p.getProperty("user.home"));
        LOG.info("home: " + p.getProperty("user.home"));
        assertNotNull(p.getProperty("java.home"));
        LOG.info("java home: " + p.getProperty("java.home"));
        assertNotNull(p.getProperty("java.runtime.version"));
        LOG.info("java runtime version: " + p.getProperty("java.runtime.version"));
    }
    
    @Test
    public void testCurrentTime() {
        LOG.info(String.format("System.currentTimeMillis() = %,d", System.currentTimeMillis()));
    }
}
