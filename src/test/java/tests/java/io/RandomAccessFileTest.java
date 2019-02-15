/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.github.jjYBdx4IL.utils.env.Maven;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class RandomAccessFileTest {

    @Test
    public void testOverwriteFileParts() throws IOException {
        System.getProperties().list(System.out);
        File f = new File(Maven.getTempTestDir(getClass()), "123");
        try (OutputStream os = new FileOutputStream(f)) {
            IOUtils.write("aa", os, "UTF-8");
        }
        try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
            raf.writeBytes("b");
        }
        try (InputStream is = new FileInputStream(f)) {
            assertEquals("ba", IOUtils.toString(is, "UTF-8"));
        }
    }
}
