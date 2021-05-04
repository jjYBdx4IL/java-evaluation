package tests.java.net;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.net.IDN;

public class IDNTest {

    @Test
    public void test() {
        assertEquals("www.abc.de", IDN.toASCII("www.abc.de"));
        assertEquals("xn--tda", IDN.toASCII("ü"));
        assertEquals("xn--ab-xka.de", IDN.toASCII("aüb.de"));
    }
}
