package org.apache.commons.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.Test;

import com.github.jjYBdx4IL.utils.env.Maven;

/**
 *
 * @author jjYBdx4IL
 */
public class FileUtilsTest {

    private static final File TEST_DIR = Maven.getTempTestDir(FileUtilsTest.class);

    private static final int ITERATIONS = 100;
    
    @Test
    public void testCleanDirectory() throws IOException {
        File dir = TEST_DIR;
        for (int i = 0; i < 10; i++) {
            dir = new File(dir, "a");
        }

        for (int i = 0; i < ITERATIONS; i++) {
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

        for (int i = 0; i < ITERATIONS; i++) {
            assertTrue("loop #" + i, dir.mkdirs());
            FileUtils.deleteDirectory(TEST_DIR);
            assertFalse(TEST_DIR.exists());
        }
    }

    @Test
    public void testMySimpleDelete() throws IOException, InterruptedException {
        File dir = TEST_DIR;
        for (int i = 0; i < 10; i++) {
            dir = new File(dir, Integer.toString(i));
        }

        for (int i = 0; i < ITERATIONS; i++) {
            assertTrue("loop #" + i, dir.mkdirs());
            mySimpleDelete(Paths.get(TEST_DIR.getAbsolutePath()));
            assertFalse(TEST_DIR.exists());
        }
    }

    private void mySimpleDelete(Path file) throws IOException, InterruptedException {
        Files.walkFileTree(file, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
