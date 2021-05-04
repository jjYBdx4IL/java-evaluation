package tests.java.lang;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SwitchTest {

    @Test
    public void testDefaultPrecedence() {
        int i = 0;
        switch(i) {
            default: break;
            case 0:
                i++;
        }
        assertEquals(1, i);
    }
}
