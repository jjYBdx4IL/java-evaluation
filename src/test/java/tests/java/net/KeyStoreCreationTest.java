package tests.java.net;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class KeyStoreCreationTest {

    private static final Logger LOG = LoggerFactory.getLogger(KeyStoreCreationTest.class);

    @SuppressWarnings("unused")
    @Test
    public void test() throws Exception {
        File keyStoreLoc = new File(Maven.getTempTestDir(KeyStoreCreationTest.class), "keystore");
        LOG.info(keyStoreLoc.getAbsolutePath());
        keyStoreLoc.getParentFile().mkdirs();
        if (keyStoreLoc.exists()) {
            assertTrue(keyStoreLoc.delete());
        }

        Class<?> keytoolClazz = Class.forName("sun.security.tools.keytool.Main");
        Method mainMethod = keytoolClazz.getMethod("main", String[].class);
        LOG.info("" + mainMethod);
        mainMethod.invoke(null, (Object)new String[]{
            "-genkey",
            "-alias", "james",
            "-keyalg", "RSA",
            "-keystore", keyStoreLoc.getAbsolutePath(),
            "-validity", "123",
            "-keypass", "nopass",
            "-storepass", "nopass",
            "-dname", "CN=localhost"
        });

        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream is = new FileInputStream(keyStoreLoc)) {
            keyStore.load(is, "nopass".toCharArray());
        }
        assertTrue(keyStore.containsAlias("james"));
        X509Certificate cert = (X509Certificate) keyStore.getCertificate("james");
        Key key = keyStore.getKey("james", "nopass".toCharArray());
        cert.checkValidity();
        assertEquals("CN=localhost", cert.getSubjectDN().getName());

        assertTrue(keyStoreLoc.delete());
    }
}
