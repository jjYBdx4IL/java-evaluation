package org.jasypt;

import static org.junit.Assert.*;

import org.jasypt.util.binary.StrongBinaryEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class EncryptionTest {

    @Test
    public void text() {
        String password = "password";
        String text = "text";
        StrongTextEncryptor encryptor = new StrongTextEncryptor();
        encryptor.setPassword(password);
        String myEncryptedText = encryptor.encrypt(text);
        String decryptedText = encryptor.decrypt(myEncryptedText);
        assertEquals(text, decryptedText);
    }

    @Test
    public void binary() throws UnsupportedEncodingException {
        String password = "password";
        String text = "text";
        StrongBinaryEncryptor encryptor = new StrongBinaryEncryptor();
        encryptor.setPassword(password);
        byte[] myEncryptedData = encryptor.encrypt(text.getBytes("UTF-8"));
        String decryptedText = new String(encryptor.decrypt(myEncryptedData), "UTF-8");
        assertEquals(text, decryptedText);
    }
}
