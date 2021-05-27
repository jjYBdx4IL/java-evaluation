package tests.java.security;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordBasedEncryptionTest {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordBasedEncryptionTest.class);
    
    public static final int SALT_LENGTH = 8; // in bytes
    public static final int AES_KEY_SIZE = 256; // in bits
    public static final int GCM_NONCE_LENGTH = 12; // in bytes
    public static final int GCM_TAG_LENGTH = 16; // in bytes
    
    // https://crypto.stackexchange.com/questions/35727/does-aad-make-gcm-encryption-more-secure
    public static final String AAD = "protv1"; // non-encrypted, but authenticated data that must match on both ends
    
    SecureRandom random = new SecureRandom();
    
    // https://bugs.openjdk.java.net/browse/JDK-8062828
    @Test
    public void testPasswordBasedEncryption() throws Throwable {
        final byte[] salt = createSalt();
        SecretKey secret = createKeyFromPwd("password", salt);
        
        // GENERATE random nonce (number used once)
        final byte[] nonce = getNonce();
        byte[] ciphertext = encrypt("Hello, World!", secret, nonce);

        // ciphertext, salt, nonce, aad go over the wire (assumed to be visible/not secret), password doesn't (secret)
        byte[] decrypted = decrypt(ciphertext, secret, nonce);
        String plaintext = new String(decrypted, StandardCharsets.UTF_8);
        LOG.info(plaintext);
        assertEquals("Hello, World!", plaintext);
    }
    
    private byte[] decrypt (byte[] ciphertext, SecretKey secret, byte[] nonce) throws Throwable {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        cipher.init(Cipher.DECRYPT_MODE, secret, spec);
        cipher.updateAAD(AAD.getBytes());
        return cipher.doFinal(ciphertext);
    }
    
    private byte[] encrypt(String message, SecretKey secret, byte[] nonce) throws Throwable {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, secret, spec);
        cipher.updateAAD(AAD.getBytes());
        return cipher.doFinal("Hello, World!".getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey createKeyFromPwd(String password, byte[] salt) throws Throwable {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        KeySpec keyspec = new PBEKeySpec(password.toCharArray(), salt, 65536, AES_KEY_SIZE);
        SecretKey tmp = factory.generateSecret(keyspec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private byte[] getNonce() {
        byte[] nonce = new byte[GCM_NONCE_LENGTH];
        random.nextBytes(nonce);
        return nonce;
    }
    
    private byte[] createSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
}
