package org.apache.tools.ant;

import java.io.File;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class DirectoryScannerTest {

    private static final Logger LOG = LoggerFactory.getLogger(DirectoryScannerTest.class);
    
    @Test
    public void test() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{"**/*.java"});
        scanner.setBasedir(new File(System.getProperty("basedir"), "src"));
        scanner.setCaseSensitive(false);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        for (String s : files) {
            LOG.debug(s);
        }
    }
}
