package tests.java.security;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestTest {

    @Test
    public void testSha1() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");
        assertEquals("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3",
            Hex.encodeHexString(shaDigest.digest("test".getBytes("UTF-8"))));
    }
}
