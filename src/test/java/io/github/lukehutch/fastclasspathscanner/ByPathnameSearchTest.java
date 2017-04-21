package io.github.lukehutch.fastclasspathscanner;

import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchProcessor;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ByPathnameSearchTest {

    private static final Logger LOG = LoggerFactory.getLogger(ByPathnameSearchTest.class);

    @Test
    public void test1() {
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
}
