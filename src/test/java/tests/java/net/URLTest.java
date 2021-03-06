package tests.java.net;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.env.Surefire;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author jjYBdx4IL
 */
public class URLTest {

    private static final Logger LOG = LoggerFactory.getLogger(URLTest.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(URLTest.class);

    @Before
    public void before() throws IOException {
        FileUtils.cleanDirectory(TEMP_DIR);
    }

    @Test
    public void testURLParsing() throws MalformedURLException {
        URL url = new URL("ftp://user:password@host.com/some/file.txt");
        assertEquals("ftp", url.getProtocol());
        assertEquals(-1, url.getPort());
        assertEquals("user:password", url.getUserInfo());
        assertEquals(21, url.getDefaultPort());
        assertEquals("/some/file.txt", url.getPath());
    }

    @Test
    public void testUrlComposition() throws MalformedURLException {
        URL base = new URL("http://some.server.de/pub/");
        assertEquals("http://some.server.de/test", new URL(base, "/test").toString());
        assertEquals("http://some.server.de/test", new URL(base, "../test").toString());
        assertEquals("http://www.google.de", new URL(base, "http://www.google.de").toString());
    }

    @Test
    public void testUrlDecomposition() throws MalformedURLException {
        URL url = new URL("http://some.server.de/pub/?q&a=1");
        assertEquals(-1, url.getPort());
        assertEquals(80, url.getDefaultPort());
        assertEquals("some.server.de", url.getHost());
        assertEquals("/pub/", url.getPath());
        assertEquals("http", url.getProtocol());
        assertEquals("q&a=1", url.getQuery());
        assertEquals("/pub/?q&a=1", url.getFile());

        url = new URL("hTTp://some.server.DE/Pub/");
        assertEquals(-1, url.getPort());
        assertEquals(80, url.getDefaultPort());
        assertEquals("some.server.DE", url.getHost());
        assertEquals("/Pub/", url.getPath());
        assertEquals("http", url.getProtocol());
        assertNull(url.getQuery());

        url = new URL("http://some.server.de");
        assertEquals("", url.getFile());

        url = new URL("http://some.server.de/");
        assertEquals("/", url.getFile());

        url = new URL("http://some.server.de:81/pub/");
        assertEquals(81, url.getPort());
        assertEquals(80, url.getDefaultPort());

        url = new URL("https://some.server.de:444/pub/");
        assertEquals(444, url.getPort());
        assertEquals(443, url.getDefaultPort());

        url = new URL("file:/home/bla");
        assertEquals("/home/bla", url.getPath());
        assertEquals("file", url.getProtocol());

        url = new URL("file:/C:\\home\\bla");
        // assertEquals("/C:/home/bla", url.getPath());
        assertEquals("file", url.getProtocol());
    }

    @Test
    public void testPathEncoding() throws MalformedURLException, URISyntaxException {
        URL url = new URL("http://test/some path");
        assertEquals("http://test/some path", url.toExternalForm());
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                url.getQuery(), url.getRef());
        assertEquals("/some path", uri.getPath());
        url = uri.toURL();
        assertEquals("http://test/some%20path", url.toExternalForm());
    }

    @Test
    public void testUrlSyntaxCheck() {
        assertInvalidURL("me@localhost");
        assertValidURL("mailto:me@localhost");
        assertValidURL("mailto:me");
    }

    public static void assertValidURL(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException ex) {
            throw new AssertionError(ex);
        }
    }

    public static void assertInvalidURL(String url) {
        try {
            new URL(url);
            fail();
        } catch (MalformedURLException ex) {
        }
    }

    @Test
    public void testGetContentFromLocalFile() throws IOException {
        File htmlFile = new File(TEMP_DIR, "test.html");
        FileUtils.writeStringToFile(htmlFile, "test content", "ASCII");
        URL url = htmlFile.toURI().toURL();

        Object result = null;
        try {
            result = url.getContent();

            assertNotNull(result);
            assertTrue(result instanceof InputStream);
        } finally {
            if (result != null && result instanceof Closeable) {
                ((Closeable) result).close();
            }
        }
    }

    // -Dhttps.proxyHost=xyz -Dhttps.proxyPort=3128
    // (-Djava.net.useSystemProxies=true)
    @Test
    public void testRetrieveRemoteCertDetails() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());
        
        URL u = new URL("https://www.google.com");
        HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
        try (InputStream is = conn.getInputStream()) {
            conn.connect();
            LOG.info("peer principal: " + conn.getPeerPrincipal());
            X509Certificate c = (X509Certificate) conn.getServerCertificates()[0];
            LOG.info("notAfter: " + c.getNotAfter());
        } 
    }
    
    // -Dhttp.proxyHost=xyz -Dhttp.proxyPort=3128
    // (-Djava.net.useSystemProxies=true)
    @Test
    public void testSimpleRemoteGet() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());
        
        URL u = new URL("http://www.google.com");
        try (InputStream is = u.openStream()) {
            LOG.info("{}", IOUtils.toString(is, UTF_8));
        } 
    }
}
