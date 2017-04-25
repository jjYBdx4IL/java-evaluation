package org.apache.commons.io;

import com.github.jjYBdx4IL.test.FileUtil;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class FileUtilsTest {

    private static final File TEST_DIR = FileUtil.createMavenTestDir(FileUtilsTest.class);

    @Test
    public void testCleanDirectory() throws IOException {
        File dir = TEST_DIR;
        for (int i = 0; i < 10; i++) {
            dir = new File(dir, "a");
        }

        for (int i = 0; i < 1000; i++) {
            assertTrue("loop #" + i, dir.mkdirs());
            FileUtils.cleanDirectory(TEST_DIR);
            assertFalse(new File(TEST_DIR, "a").exists());
        }
    }
    
    @Test
    public void testDeleteDirectory() throws IOException {
        File dir = TEST_DIR;
        for (int i = 0; i < 10; i++) {
            dir = new File(dir, "a");
        }

        for (int i = 0; i < 1000; i++) {
            assertTrue("loop #" + i, dir.mkdirs());
            FileUtils.deleteDirectory(TEST_DIR);
            assertFalse(TEST_DIR.exists());
        }
    }
}
