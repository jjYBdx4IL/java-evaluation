package tests.java.nio.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class PathTest {

    private static final Logger LOG = LoggerFactory.getLogger(PathTest.class);

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
}
