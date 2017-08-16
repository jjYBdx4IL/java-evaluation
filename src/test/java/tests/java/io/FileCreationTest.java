package tests.java.io;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FileCreationTest {

    private final static File tempDir = FileUtil.createMavenTestDir(FileCreationTest.class);

    @Before
    public void beforeTest() {
        FileUtil.provideCleanDirectory(tempDir);
    }

    @Test
    public void testTruncateOnCreate() throws FileNotFoundException, IOException {
        byte[] ba = new byte[1024];
        File f = new File(tempDir, "testFile.txt");
        try (OutputStream os = new FileOutputStream(f)) {
            IOUtils.write(ba, os);
        }
        assertEquals(ba.length, f.length());

        try (OutputStream os = new FileOutputStream(f)) {
            IOUtils.write("123".getBytes(), os);
        }
        assertEquals(3, f.length());
    }
}
