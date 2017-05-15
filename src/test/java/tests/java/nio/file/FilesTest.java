package tests.java.nio.file;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FilesTest {

    private static final File TEMP_DIR = FileUtil.createMavenTestDir(FilesTest.class);

    @Before
    public void beforeTest() throws IOException {
        FileUtil.provideCleanDirectory(TEMP_DIR);
    }

    @Test
    public void testExists() {
        assertTrue(Files.exists(TEMP_DIR.toPath()));
    }

}
