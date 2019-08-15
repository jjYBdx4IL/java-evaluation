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
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] digest = sha1.digest("test".getBytes("UTF-8"));
        assertEquals("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", Hex.encodeHexString(digest));
        assertEquals(20, digest.length);
    }

    @Test
    public void testMd5() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest("test".getBytes("UTF-8"));
        assertEquals("098f6bcd4621d373cade4e832627b4f6", Hex.encodeHexString(digest));
        assertEquals(16, digest.length);
    }
}
