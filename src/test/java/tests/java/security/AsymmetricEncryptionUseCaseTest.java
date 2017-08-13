/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class AsymmetricEncryptionUseCaseTest {

    private static final Logger log = LoggerFactory.getLogger(AsymmetricEncryptionUseCaseTest.class);
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final int KEY_SAVE_RADIX = 16;
    private static final String ENC_ALGO = "RSA";
    private static final String TEST_DATA = "test input";

    @Test
    public void test1() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        // generate key pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ENC_ALGO);
        kpg.initialize(2048);
        KeyPair kp = kpg.genKeyPair();
        Key publicKey = kp.getPublic();
        Key privateKey = kp.getPrivate();

        // encrypt
        Cipher cipher = Cipher.getInstance(ENC_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherData = cipher.doFinal(TEST_DATA.getBytes(CHARSET));
        log.info("unencrypted: " + new BigInteger(TEST_DATA.getBytes(CHARSET)).toString(16));
        log.info("  encrypted: " + new BigInteger(cipherData).toString(16));

        // save key pair
        KeyFactory fact = KeyFactory.getInstance(ENC_ALGO);
        RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
                RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
                RSAPrivateKeySpec.class);

        Properties props = new Properties();
        props.put("pubMod", pub.getModulus().toString(KEY_SAVE_RADIX));
        props.put("pubExp", pub.getPublicExponent().toString(KEY_SAVE_RADIX));
        props.put("privateMod", priv.getModulus().toString(KEY_SAVE_RADIX));
        props.put("privateExp", priv.getPrivateExponent().toString(KEY_SAVE_RADIX));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        props.storeToXML(baos, "comment", CHARSET.name());

        String savedKeys = baos.toString(CHARSET.name());

        log.info(savedKeys);

        // load key pair
        fact = KeyFactory.getInstance(ENC_ALGO);
        props = new Properties();
        props.loadFromXML(new ByteArrayInputStream(savedKeys.getBytes(CHARSET)));

        publicKey = fact.generatePublic(new RSAPublicKeySpec(
                new BigInteger(props.getProperty("pubMod"), KEY_SAVE_RADIX),
                new BigInteger(props.getProperty("pubExp"), KEY_SAVE_RADIX)));
        privateKey = fact.generatePrivate(new RSAPrivateKeySpec(
                new BigInteger(props.getProperty("privateMod"), KEY_SAVE_RADIX),
                new BigInteger(props.getProperty("privateExp"), KEY_SAVE_RADIX)));

        // decrypt
        cipher = Cipher.getInstance(ENC_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        cipherData = cipher.doFinal(cipherData);
        log.info("deencrypted: " + new BigInteger(cipherData).toString(16));

        assertEquals(new String(cipherData, CHARSET), TEST_DATA);
    }
}
