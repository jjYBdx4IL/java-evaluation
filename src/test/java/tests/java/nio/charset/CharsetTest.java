package tests.java.nio.charset;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CharsetTest {

    @Test
    public void test1() {
        try {
            Charset.forName("not-existing");
            fail();
        } catch(UnsupportedCharsetException ex) {}
    }
}
