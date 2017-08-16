package org.apache.commons.io;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.FileUtil;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FileUtils2Test {

    public final static File tempDir = FileUtil.createMavenTestDir(FileUtils2Test.class);
    public final static File parentDir = new File(tempDir, "parent");

    @Before
    public void beforeTest() {
        FileUtil.provideCleanDirectory(tempDir);
    }

    @Test
    public void testDeleteRecursive() throws IOException {
        assertFalse(parentDir.exists());
        
        File deepestDir = new File(parentDir, "1" + File.separator + "2" + File.separator + "3");
         
        assertTrue(deepestDir.mkdirs());
        assertTrue(deepestDir.exists());
        assertTrue(parentDir.exists());

        FileUtils.deleteDirectory(parentDir);

        assertFalse(deepestDir.exists());
        assertFalse(parentDir.exists());
    }

    @Test
    public void testDeleteRecursiveNotExisting() throws IOException {
        assertFalse(parentDir.exists());

        FileUtils.deleteDirectory(parentDir);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRecursiveParentIsFile() throws IOException {
        assertFalse(parentDir.exists());

        assertTrue(parentDir.createNewFile());
        assertTrue(parentDir.exists());
        assertTrue(parentDir.isFile());

        FileUtils.deleteDirectory(parentDir);
    }
}
