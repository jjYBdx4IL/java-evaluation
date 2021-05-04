package org.apache.commons.compress;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static org.apache.commons.compress.CompressionTest.EXPECTED_CONTENT;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class DecompressionTest {

    @Test
    public void testGZip() throws CompressorException, IOException {
        String s;
        try (InputStream input = CompressionTest.getDecompressionStreamForResource("test.gz")) {
            s = IOUtils.toString(input, "UTF-8");
        }
        assertEquals(EXPECTED_CONTENT, s);
    }

    @Test
    public void testBZip2() throws CompressorException, IOException {
        String s;
        try (InputStream input = CompressionTest.getDecompressionStreamForResource("test.bz2")) {
            s = IOUtils.toString(input, "UTF-8");
        }
        assertEquals(EXPECTED_CONTENT, s);
    }

    @Test
    public void testXZ() throws CompressorException, IOException {
        String s;
        try (InputStream input = CompressionTest.getDecompressionStreamForResource("test.xz")) {
            s = IOUtils.toString(input, "UTF-8");
        }
        assertEquals(EXPECTED_CONTENT, s);
    }

    @Test
    public void test7z() throws CompressorException, IOException, URISyntaxException {
        SevenZFile sevenZFile = new SevenZFile(new File(getClass().getResource("test.7z").toURI()));
        assertNotNull(sevenZFile);
        SevenZArchiveEntry entry = sevenZFile.getNextEntry();
        assertNotNull(entry);
        assertEquals("test", entry.getName());
        byte[] content = new byte[(int) entry.getSize()];
        int nRead = 0;
        while (nRead < entry.getSize()) {
            int n = sevenZFile.read(content, nRead, (int) entry.getSize() - nRead);
            if (n < 0) {
                fail();
            }
            nRead += n;
        }
        assertEquals(EXPECTED_CONTENT, new String(content));
    }
}
