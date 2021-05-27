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

import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class KeyStoreCreationTest {

    private static final Logger LOG = LoggerFactory.getLogger(KeyStoreCreationTest.class);
    private static final File keyStoreLoc = new File(Maven.getTempTestDir(KeyStoreCreationTest.class), "keystore");
    private static final String STORE_PASS = "storepass";
    private static final String KEY_PASS = "keypass";

    private static final String CERTIFICATE_ALIAS = "james";
    private static final String DN = "localhost";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    @Test
    public void test() throws Exception {
        LOG.info(keyStoreLoc.getAbsolutePath());
        keyStoreLoc.getParentFile().mkdirs();
        if (keyStoreLoc.exists()) {
            assertTrue(keyStoreLoc.delete());
        }

        createKeyStore();

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream is = new FileInputStream(keyStoreLoc)) {
            ks.load(is, STORE_PASS.toCharArray());
        }
        assertTrue(ks.containsAlias(CERTIFICATE_ALIAS));
        X509Certificate cert = (X509Certificate) ks.getCertificate(CERTIFICATE_ALIAS);
        LOG.info("cert: {}", cert);
        Key key = ks.getKey(CERTIFICATE_ALIAS, KEY_PASS.toCharArray());
        LOG.info("key: {}", key);
        cert.checkValidity();
        assertEquals("CN=" + DN, cert.getSubjectX500Principal().getName());
        LOG.info("notAfter: {}", cert.getNotAfter());

        assertTrue(keyStoreLoc.delete());
    }

    private void createKeyStore() throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, STORE_PASS.toCharArray());
        
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048);
        KeyPair keyPair = kpGen.generateKeyPair();
        X509Certificate cert = generateSelfSignedCert(keyPair, "SHA256WithRSAEncryption", DN, 366);
        saveCert(ks, cert, keyPair.getPrivate());
    }

    private void saveCert(KeyStore ks, X509Certificate cert, PrivateKey key) throws Exception {
        ks.setKeyEntry(CERTIFICATE_ALIAS, key, KEY_PASS.toCharArray(),
                new java.security.cert.Certificate[] { cert });
        try (FileOutputStream fos = new FileOutputStream(keyStoreLoc)) {
            ks.store(fos, STORE_PASS.toCharArray());
        }
    }

    // from: https://github.com/misterpki/selfsignedcert/blob/master/src/main/java/com/misterpki/SelfSignedCertGenerator.java
    public static X509Certificate generateSelfSignedCert(final KeyPair keyPair, final String hashAlgorithm,
            final String cn, final int days) throws OperatorCreationException, CertificateException, CertIOException {
        final Instant now = Instant.now();
        final Date notBefore = Date.from(now);
        final Date notAfter = Date.from(now.plus(Duration.ofDays(days)));

        final ContentSigner contentSigner = new JcaContentSignerBuilder(hashAlgorithm).build(keyPair.getPrivate());
        final X500Name x500Name = new X500Name("CN=" + cn);
        final X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(x500Name,
                BigInteger.valueOf(now.toEpochMilli()), notBefore, notAfter, x500Name, keyPair.getPublic())
                        .addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyId(keyPair.getPublic()))
                        .addExtension(Extension.authorityKeyIdentifier, false,
                                createAuthorityKeyId(keyPair.getPublic()))
                        .addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider())
                .getCertificate(certificateBuilder.build(contentSigner));
    }

    private static SubjectKeyIdentifier createSubjectKeyId(final PublicKey publicKey) throws OperatorCreationException {
        final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        final DigestCalculator digCalc = new BcDigestCalculatorProvider()
                .get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));

        return new X509ExtensionUtils(digCalc).createSubjectKeyIdentifier(publicKeyInfo);
    }

    private static AuthorityKeyIdentifier createAuthorityKeyId(final PublicKey publicKey)
            throws OperatorCreationException {
        final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        final DigestCalculator digCalc = new BcDigestCalculatorProvider()
                .get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));

        return new X509ExtensionUtils(digCalc).createAuthorityKeyIdentifier(publicKeyInfo);
    }
}
