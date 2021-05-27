package tests.java.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordCryptHashTest {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordCryptHashTest.class);
    
    SecureRandom random = new SecureRandom();
    
    @Test
    public void testPBKDF2WithHmacSHA1() throws Throwable {
        String password = "123jaöldksf jöalksdjf aüpesoi9fj pasmfd kma öäldkmsf öäaskjd fäöaklsd fäö";
        
        byte[] salt = createSalt();
        byte[] hash = createHash(password, salt);
        
        LOG.info("Password:       " + password);
        LOG.info("Salt:           " + Hex.encodeHexString(salt));
        LOG.info("Salt str len:   " + Hex.encodeHexString(salt).length());
        LOG.info("Hashed pwd:     " + Hex.encodeHexString(hash));
        LOG.info("Hashed pwd len: " + Hex.encodeHexString(hash).length());
        
        // this would be stored in your login database:
        String stored = Hex.encodeHexString(salt) + Hex.encodeHexString(hash);
        
        // then we go on to verify a password:
        salt = Hex.decodeHex(stored.substring(0,32));
        String vfyHash = stored.substring(32);
        String pwdToVerify = "wrong one";
        hash = createHash(pwdToVerify, salt);
        assertNotEquals(vfyHash, Hex.encodeHexString(hash)); // wrong pwd fails
        
        hash = createHash(password, salt);
        assertEquals(vfyHash, Hex.encodeHexString(hash)); // correct pwd succeeds
    }
    
    private byte[] createHash(String pwd, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(pwd.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }
    
    private byte[] createSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
