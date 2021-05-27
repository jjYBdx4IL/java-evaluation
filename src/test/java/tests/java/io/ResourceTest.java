package tests.java.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceTest {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceTest.class);

    @Test
    public void testListUnpackedResources() throws IOException, URISyntaxException {
        LOG.info("local (unpacked) resource dir:");
        URL res = getClass().getResource("");
        LOG.info(res.toExternalForm());
        for (File e : new File(res.toURI()).listFiles()) {
            LOG.info("  " + e);
        }

    }

    @Test
    public void testListJarResources() throws IOException, URISyntaxException {
        LOG.info("jar file resource dir:");
        URL res = LOG.getClass().getResource("");
        LOG.info(res.toExternalForm());

        String j = res.toExternalForm();

        assertTrue(j.startsWith("jar:file:"));
        j = j.substring(9);

        String innerPrefix = "";
        if (j.indexOf("!") != -1) {
            innerPrefix = j.substring(j.indexOf("!") + 1);
            j = j.substring(0, j.indexOf("!"));
        }
        assertEquals("/org/slf4j/impl/", innerPrefix);
        assertTrue(j.endsWith(".jar"));

        try (JarFile jarfile = new JarFile(j)) {
            Enumeration<JarEntry> jes = jarfile.entries();
            while (jes.hasMoreElements()) {
                JarEntry je = jes.nextElement();
                LOG.info(je.getRealName());
            }

            JarEntry je = jarfile.getJarEntry("META-INF/maven/org.slf4j/slf4j-simple/pom.properties");
            LOG.info(IOUtils.toString(jarfile.getInputStream(je), StandardCharsets.UTF_8));
        }
    }
}
