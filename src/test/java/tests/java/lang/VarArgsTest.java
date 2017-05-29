package tests.java.lang;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class VarArgsTest {

    @Test
    public void testStringFormat() {
        assertEquals("1 a", String.format("%d %s", 1, "a", "b"));
    }
}
