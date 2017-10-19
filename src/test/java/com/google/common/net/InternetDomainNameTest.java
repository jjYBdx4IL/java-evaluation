package com.google.common.net;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InternetDomainNameTest {

    @Test
    public void test() {
        assertTrue(InternetDomainName.isValid("ibm.d"));
        assertTrue(InternetDomainName.isValid("localhost"));
        assertTrue(InternetDomainName.isValid("surely.not.existing.a123781238923.com"));
        assertFalse(InternetDomainName.isValid(".me.localhost"));
        assertTrue(InternetDomainName.isValid("me.localhost."));
    }
}
