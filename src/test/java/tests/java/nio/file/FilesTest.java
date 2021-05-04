package tests.java.nio.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.github.jjYBdx4IL.utils.env.Maven;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FilesTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(FilesTest.class);

    @Before
    public void beforeTest() throws IOException {
        FileUtils.cleanDirectory(TEMP_DIR);
    }

    @Test
    public void testExists() {
        assertTrue(Files.exists(TEMP_DIR.toPath()));
    }

}
