package tests.java.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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
    private final static File TEMP_DIR = Maven.getTempTestDir(FileTest.class);    

    @Test
    public void testConstructorPathSeparatorHandling() {
        File f = new File("a", "b/c");
        assertEquals("a/b/c".replace("/", File.separator), f.getPath());
        f = new File("a", "b\\c");
        assertEquals("a/b\\c".replace("/", File.separator), f.getPath());
    }

    @Test
    public void testGetCanonicalPath() throws IOException {
        @SuppressWarnings("deprecation")
        File targetDir = new File(Maven.getMavenTargetDir().getCanonicalPath() + File.separator);
        LOG.debug(targetDir.getCanonicalPath());
        assertFalse(targetDir.getCanonicalPath().endsWith(File.separator));
    }
    
    @Test
    public void testGetParent() {
        assertEquals("a", new File("a/b").getParent());
        assertNull(new File("b").getParent());
    }
    
    @Test
    public void testDelete() {
        // test delete of non-existing file
        File f1 = new File(TEMP_DIR, "not-existing-file-system-entry");
        assertFalse(f1.delete());
    }
    
    @Test
    public void testRename() throws IOException {
        File f1 = new File(TEMP_DIR, "testRename1");
        File f2 = new File(TEMP_DIR, "testRename2");
        
        assertEquals("testRename1", f1.getName());
        assertEquals("testRename2", f2.getName());
        assertNotEquals(f1.getAbsolutePath(), f2.getAbsolutePath());
        FileUtils.write(f1, "1", "UTF-8");
        FileUtils.write(f2, "2", "UTF-8");
        assertTrue(f1.exists());
        assertTrue(f2.exists());
        assertEquals("1", FileUtils.readFileToString(f1, "UTF-8"));
        assertEquals("2", FileUtils.readFileToString(f2, "UTF-8"));
        
        f1.renameTo(f2);
        
        assertFalse(f1.exists());
        assertTrue(f2.exists());
        assertNotEquals(f1.getAbsolutePath(), f2.getAbsolutePath());
        assertEquals("testRename1", f1.getName()); // f1 still points to the same file name after the rename!
        assertEquals("testRename2", f2.getName());
        assertEquals("1", FileUtils.readFileToString(f2, "UTF-8"));
    }
}
