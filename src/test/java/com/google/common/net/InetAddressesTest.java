package com.google.common.net;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InetAddressesTest {

    @Test
    public void test() {
        assertTrue(InetAddresses.isInetAddress("127.0.0.1"));
        assertTrue(InetAddresses.isInetAddress("::1"));
        assertFalse(InetAddresses.isInetAddress("ibm.com"));
        assertFalse(InetAddresses.isInetAddress("127.0.0"));
    }
}
