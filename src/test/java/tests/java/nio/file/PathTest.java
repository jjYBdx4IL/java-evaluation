package tests.java.nio.file;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class PathTest {

    @Test
    public void testResolve() {
        Path root = Paths.get("/root");

        assertEquals(Paths.get("/root/rel"), root.resolve(Paths.get("rel")));
        assertEquals(Paths.get("/rel"), root.resolve(Paths.get("/rel")));
    }

    @Test
    public void testRelativize() {
        Path root = Paths.get("/root");
        assertEquals(Paths.get("rel"), root.relativize(Paths.get("/root/rel")));
        assertEquals(Paths.get("../rel"), root.relativize(Paths.get("/rel")));
        try {
            root.relativize(Paths.get("rel"));
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }
    
    @Test
    public void testToString() {
        // converts to system specific format:
        assertEquals(File.separator + "root", Paths.get("/root").toString());
        
        // same
        assertEquals(File.separator + "root", Paths.get("/root").toFile().toString());
    }
    
    @Test
    public void testNormalize() {
        Path p = Paths.get("/a/b/.");
        assertEquals(3, p.getNameCount());
        assertEquals(2, p.normalize().getNameCount());
    }
    
    @Test
    public void countCompoments() {
        assertEquals(2, Paths.get("/a////b").getNameCount());
        assertEquals(2, Paths.get("a//b").getNameCount());
    }
}
