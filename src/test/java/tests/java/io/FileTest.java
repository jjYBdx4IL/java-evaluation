package tests.java.io;

import com.github.jjYBdx4IL.test.FileUtil;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FileTest {

    private static final Logger LOG = LoggerFactory.getLogger(FileTest.class);

    @Test
    public void testConstructorPathSeparatorHandling() {
        File f = new File("a", "b/c");
        assertEquals("a/b/c".replace("/", File.separator), f.getPath());
        f = new File("a", "b\\c");
        assertEquals("a/b\\c".replace("/", File.separator), f.getPath());
    }

    @Test
    public void testGetCanonicalPath() throws IOException {
        File targetDir = new File(FileUtil.getMavenTargetDir().getCanonicalPath() + File.separator);
        LOG.debug(targetDir.getCanonicalPath());
        assertFalse(targetDir.getCanonicalPath().endsWith(File.separator));
    }
}
