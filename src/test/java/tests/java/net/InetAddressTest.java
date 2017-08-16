package tests.java.net;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class InetAddressTest {
    @Test
    public void test() throws UnknownHostException {
        assertEquals("242.168.0.1", InetAddress.getByName("242.168.0.001").getHostAddress());
        assertEquals("1.0.0.1", InetAddress.getByName("01.0.0.001").getHostAddress());
        assertEquals("0.0.0.1", InetAddress.getByName("00.0.0.001").getHostAddress());
    }
}
