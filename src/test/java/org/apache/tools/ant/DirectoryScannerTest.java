package org.apache.tools.ant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author jjYBdx4IL
 */
public class DirectoryScannerTest {

    private static final Logger LOG = LoggerFactory.getLogger(DirectoryScannerTest.class);

    @Test
    public void test() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[] { "**/*.java" });
        scanner.setBasedir(new File(System.getProperty("basedir"), "src"));
        scanner.setCaseSensitive(false);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        assertTrue(files.length > 100);
        for (String s : files) {
            LOG.debug(s);
        }
    }

    /**
     * default excludes not used by default...
     * 
     * @throws IOException
     */
    @Test
    public void testDefaultExcludes() throws IOException {
        File tmpDir = Maven.getTempTestDir(DirectoryScannerTest.class);
        FileUtils.write(new File(tmpDir, "a"), "123", "ASCII", false);
        FileUtils.write(new File(tmpDir, "a~"), "123", "ASCII", false);

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(tmpDir);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        assertEquals(2, files.length);
        assertTrue(ArrayUtils.contains(files, "a"));
        assertTrue(ArrayUtils.contains(files, "a~"));

        scanner = new DirectoryScanner();
        scanner.setExcludes(DirectoryScanner.getDefaultExcludes());
        scanner.setBasedir(tmpDir);
        scanner.scan();
        files = scanner.getIncludedFiles();
        assertEquals(1, files.length);
        assertTrue(ArrayUtils.contains(files, "a"));
    }
}
