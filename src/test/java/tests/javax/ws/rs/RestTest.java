package tests.javax.ws.rs;

import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.io.IoUtils;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class RestTest extends RestTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(RestTest.class);

    @Test
    public void testHello() throws Exception {
        URL url = getServer().getURL("hello");
        LOG.info("server URL: " + url);
        String pageContents = IOUtils.toString(url, "ASCII");
        LOG.info("test page contents: " + pageContents);
        assertEquals("<html> <title>Hello Jersey</title><body><h1>Hello Jersey</body></h1></html> ", pageContents);
        assertEquals("Hello Jersey", IoUtils.toString(url, "text/plain; charset=ASCII"));
    }

    @Test
    public void testCustomResponseType() throws Exception {
        URL url = getServer().getURL("customResponseType");
        String pageContents = IoUtils.toString(url, "text/plain; charset=ASCII");
        assertEquals("CustomType serialized", pageContents);
    }

}
