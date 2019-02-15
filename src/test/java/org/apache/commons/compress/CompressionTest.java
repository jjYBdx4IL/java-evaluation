package org.apache.commons.compress;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CompressionTest {

    public static final String EXPECTED_CONTENT = "test\n";

    public static InputStream getDecompressionStreamForResource(String filename) throws CompressorException {
        return new CompressorStreamFactory()
                .createCompressorInputStream(CompressionTest.class.getResourceAsStream(filename));
    }

    public static InputStream getDecompressionStreamForFile(File file) throws CompressorException, FileNotFoundException {
        return new CompressorStreamFactory()
                .createCompressorInputStream(new BufferedInputStream(new FileInputStream(file)));
    }

    public static File getTempFile(String filename) {
        return new File(new File(System.getProperty("basedir", null), "target"), filename);
    }

    public static OutputStream getCompressorOutputStream(String algo, File file) throws FileNotFoundException, CompressorException {
        return new CompressorStreamFactory().createCompressorOutputStream(algo, new FileOutputStream(file));
    }

    private void test(String algo) throws FileNotFoundException, CompressorException, IOException {
        File file = getTempFile("test." + algo);
        String s;

        try (OutputStream os = getCompressorOutputStream(algo, file)) {
            IOUtils.write(EXPECTED_CONTENT, os, "UTF-8");
        }
        try (InputStream is = getDecompressionStreamForFile(file)) {
            s = IOUtils.toString(is, "UTF-8");
        }

        assertEquals(EXPECTED_CONTENT, s);
    }

    @Test
    public void testGZip() throws CompressorException, IOException {
        test("gz");
    }

    @Test
    public void testBZip2() throws CompressorException, IOException {
        test("bzip2");
    }

    @Test
    public void testXZ() throws CompressorException, IOException {
        test("xz");
    }
}
