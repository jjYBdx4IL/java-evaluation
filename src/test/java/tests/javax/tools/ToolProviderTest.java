package tests.javax.tools;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ToolProviderTest {

    private static final Logger LOG = LoggerFactory.getLogger(ToolProviderTest.class);
    
    @Test
    public void testGetJavaCompiler() {
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        assertNotNull(jc);
    }
}
