package org.apache.commons.io;

import static com.github.jjYBdx4IL.utils.io.FindUtils.globOne;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FileWalkerTest {

    private static final Logger LOG = LoggerFactory.getLogger(FileWalkerTest.class);

    @Test
    public void testSuffixFileFilter() throws IOException {
        final AtomicInteger count = new AtomicInteger();

        SuffixFilterFileWalker walker = new SuffixFilterFileWalker(".txt") {
            @Override
            protected void handleFile(File file, int depth, Collection<File> results) throws IOException {
                LOG.debug(file.getAbsolutePath());
                count.incrementAndGet();
            }
        };
        List<File> files = new ArrayList<>();
        walker.walkIt(globOne("/src/test/**/" + FileWalkerTest.class.getSimpleName() + ".dir/"), files);
        assertTrue(files.isEmpty());
        assertEquals(3, count.get());
    }

    public static class SuffixFilterFileWalker extends DirectoryWalker<File> {
        public SuffixFilterFileWalker(String suffix) {
            super(null, FileFilterUtils.suffixFileFilter(suffix), 50);
        }

        public void walkIt(File startDirectory, Collection<File> files)
            throws IOException {
            // walk is "protected final"
            walk(startDirectory, files);
        }
    }

}
