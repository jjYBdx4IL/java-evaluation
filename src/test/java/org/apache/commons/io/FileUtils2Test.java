package org.apache.commons.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.github.jjYBdx4IL.utils.env.Maven;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FileUtils2Test {

    public final static File tempDir = Maven.getTempTestDir(FileUtils2Test.class);
    public final static File parentDir = new File(tempDir, "parent");

    @Before
    public void beforeTest() throws IOException {
        FileUtils.cleanDirectory(tempDir);
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
