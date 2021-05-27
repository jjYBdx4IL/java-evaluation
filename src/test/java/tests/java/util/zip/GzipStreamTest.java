package tests.java.util.zip;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.env.Maven;

public class GzipStreamTest {

    private static File basedir = new File(Maven.getBasedir(GzipStreamTest.class));
    private static final Logger LOG = LoggerFactory.getLogger(GzipStreamTest.class);
    
    @Test
    public void testGzipStreams() throws IOException {
        File pomXml = new File(basedir, "pom.xml");
        String pomXmlContent = FileUtils.readFileToString(pomXml, StandardCharsets.UTF_8);
        LOG.info("input size: " + pomXmlContent.length());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            IOUtils.write(pomXmlContent.getBytes(StandardCharsets.UTF_8), gzos);
        }
        
        byte[] compressed = baos.toByteArray();
        LOG.info("compressed size: " + compressed.length);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        try (GZIPInputStream gzis = new GZIPInputStream(bais)) {
            IOUtils.copy(gzis, baos2);
        }
        
        String decompressed = baos2.toString(StandardCharsets.UTF_8);
        assertEquals(pomXmlContent, decompressed);
    }

}
