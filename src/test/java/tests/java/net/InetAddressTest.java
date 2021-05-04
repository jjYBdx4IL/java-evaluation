package tests.java.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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

public class InetAddressTest {
    @Test
    public void test() throws UnknownHostException {
        assertEquals("242.168.0.1", InetAddress.getByName("242.168.0.001").getHostAddress());
        assertEquals("1.0.0.1", InetAddress.getByName("01.0.0.001").getHostAddress());
        assertEquals("0.0.0.1", InetAddress.getByName("00.0.0.001").getHostAddress());
    }
    
    @Test
    public void testIpv6() throws UnknownHostException {
        assertEquals("fe80:0:0:0:6ef0:49ff:fee1:abbf", InetAddress.getByName("fe80::6ef0:49ff:fee1:abbf").getHostAddress());
        assertEquals("2a02:8109:9d80:7277:6ef0:49ff:fee1:abbf", InetAddress.getByName("2a02:8109:9d80:7277:6ef0:49ff:fee1:abbf").getHostAddress());
        
        assertEquals("0:0:0:0:0:0:0:1", InetAddress.getByName("::1").getHostAddress());
        assertFalse(InetAddress.getByName("::1").isAnyLocalAddress());
        assertFalse(InetAddress.getByName("::1").isLinkLocalAddress());
        assertTrue(InetAddress.getByName("::1").isLoopbackAddress());
        assertFalse(InetAddress.getByName("::1").isSiteLocalAddress());
    }
}
