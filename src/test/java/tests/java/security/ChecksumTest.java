/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.security;

import com.github.jjYBdx4IL.utils.format.DigestFormatter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.schmizz.sshj.common.IOUtils;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ChecksumTest {

    public static final String TEST_INPUT = "abc";

    @Test
    public void testMD5() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        assertNotNull(md);
        byte[] digest = md.digest(TEST_INPUT.getBytes("ASCII"));

        assertEquals("900150983cd24fb0d6963f7d28e17f72", DigestFormatter.md5(digest));
    }

    @Test
    public void testSHA1() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        assertNotNull(sha1);
        byte[] digest = sha1.digest(TEST_INPUT.getBytes("ASCII"));

        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestFormatter.sha1(digest));
    }

    @Test
    public void testStreamDecorator() throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        assertNotNull(md);
        try (InputStream is = new ByteArrayInputStream(TEST_INPUT.getBytes("ASCII"));
                DigestInputStream dis = new DigestInputStream(is, md)) {
            IOUtils.readFully(dis);
        }
        byte[] digest = md.digest();

        assertEquals("900150983cd24fb0d6963f7d28e17f72", DigestFormatter.md5(digest));
    }

}
