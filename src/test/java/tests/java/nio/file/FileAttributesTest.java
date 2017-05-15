package tests.java.nio.file;

import com.github.jjYBdx4IL.test.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import org.apache.commons.lang3.SystemUtils;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
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
public class FileAttributesTest {

    private static final Logger LOG = LoggerFactory.getLogger(FileAttributesTest.class);
    private static final File TEMP_DIR = FileUtil.createMavenTestDir(FileAttributesTest.class);

    @Before
    public void beforeTest() throws IOException {
        FileUtil.provideCleanDirectory(TEMP_DIR);
    }

    @Test
    public void testPosixFileAttributeView() throws IOException {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
        
        File subdir = new File(TEMP_DIR, "subdir");
        assertTrue(subdir.mkdirs());

        PosixFileAttributes attrs = Files.getFileAttributeView(subdir.toPath(), PosixFileAttributeView.class)
                .readAttributes();
        LOG.info(String.format("owner: %s", attrs.owner().getName()));
        LOG.info(String.format("perms: %s", PosixFilePermissions.toString(attrs.permissions())));
        LOG.info(String.format("creat: %s", attrs.creationTime().toString()));
    }

    @Test
    public void testBasicFileAttributeView() throws IOException {
        File subdir = new File(TEMP_DIR, "subdir");
        assertTrue(subdir.mkdirs());

        BasicFileAttributes attrs = Files.getFileAttributeView(subdir.toPath(), BasicFileAttributeView.class)
                .readAttributes();
        LOG.info(String.format("creat: %s", attrs.creationTime().toString()));
    }
}
