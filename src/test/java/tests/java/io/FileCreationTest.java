package tests.java.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.github.jjYBdx4IL.utils.env.Maven;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FileCreationTest {

    private final static File tempDir = Maven.getTempTestDir(FileCreationTest.class);

    @Before
    public void beforeTest() throws IOException {
        FileUtils.cleanDirectory(tempDir);
    }

    @Test
    public void testTruncateOnCreate() throws FileNotFoundException, IOException {
        byte[] ba = new byte[1024];
        File f = new File(tempDir, "testFile.txt");
        try (OutputStream os = new FileOutputStream(f)) {
            IOUtils.write(ba, os);
        }
        assertEquals(ba.length, f.length());

        try (OutputStream os = new FileOutputStream(f)) {
            IOUtils.write("123".getBytes(), os);
        }
        assertEquals(3, f.length());
    }
}
