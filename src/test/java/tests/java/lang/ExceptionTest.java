package tests.java.lang;

import org.junit.Test;

public class ExceptionTest {

    @Test
    public void testStacktrace() {
        Exception ex = new Exception();
        for (StackTraceElement el : ex.getStackTrace()) {
            System.out.println(el.toString());
        }
    }
}