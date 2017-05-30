package io.github.lukehutch.fastclasspathscanner;

import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchProcessor;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchProcessorWithContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ClassPathScannerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ClassPathScannerTest.class);

    @Test
    public void testFindResourcesOnClasspath() {
        FileMatchProcessor fileMatchProcessor = new FileMatchProcessor() {
            @Override
            public void processMatch(String relativePath, InputStream inputStream, long lengthBytes) throws IOException {
                LOG.debug(relativePath);
            }
        };
        FastClasspathScanner scanner = new FastClasspathScanner();
        scanner.matchFilenamePattern("^org/openimaj/image/.*\\.(png|jpg|jpeg|JPG)", fileMatchProcessor);
        scanner.scan();
    }

    @Test
    public void testFindClassesExcludingDependencies() {
        final File moduleClassesDir = new File(System.getProperty("basedir"), "target/test-classes");

        FileMatchProcessorWithContext fileMatchProcessorWithContext = new FileMatchProcessorWithContext() {
            @Override
            public void processMatch(File classpathElt, String relativePath, InputStream inputStream, long lengthBytes) throws IOException {
                if (moduleClassesDir.equals(classpathElt)) {
                    LOG.debug("class from test-classes folder: " + relativePath);
                }
            }
        };

        FastClasspathScanner scanner = new FastClasspathScanner();
        scanner.matchFilenamePattern(".*", fileMatchProcessorWithContext);
        scanner.scan();
    }
}
