package tests.java.net;

import java.net.MalformedURLException;
import java.net.URL;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class URLTest {

    private static final Logger LOG = LoggerFactory.getLogger(URLTest.class);
    
    @Test
    public void testURLParsing() throws MalformedURLException {
        URL url = new URL("ftp://user:password@host.com/some/file.txt");
        assertEquals("ftp", url.getProtocol());
        assertEquals(-1, url.getPort());
        assertEquals("user:password", url.getUserInfo());
        assertEquals(21, url.getDefaultPort());
        assertEquals("/some/file.txt", url.getPath());
    }
}
