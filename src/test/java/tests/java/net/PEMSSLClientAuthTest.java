/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class PEMSSLClientAuthTest {

    private static final Logger log = LoggerFactory.getLogger(PEMSSLClientAuthTest.class);

    private static final int VALIDITY_PERIOD = 7 * 24 * 60 * 60 * 1000; // one week
    private static final int SERVER_PORT = 9072;
    private static final long CONNECTION_TIMEOUT_MILLIS = 60000L;
    private static String caCertPEMString;
    private static String badCACertPEMString;
    private static String clientPrivateKeyPEMString;
    private static String clientCertPEMString;
    private static String badClientCertPEMString;  // good DN, bad CA
    private static String badClientCert2PEMString; // bad DN, good CA
    private static String serverPrivateKeyPEMString;
    private static String serverCertPEMString;

    static {
        Provider p = (Provider) new BouncyCastleProvider();
        //Security.insertProviderAt(p, 1);
        Security.addProvider(p);
    }

    @BeforeClass
    public static void genPEMCerts() throws Exception {
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(1024);

        // CA
        KeyPair caKeyPair = kpGen.generateKeyPair();
        X509Certificate caCert = generateRootCert(caKeyPair, "CN=Test CA Certificate", 1l);
        caCertPEMString = toPEM(caCert);

        // bad CA
        KeyPair badCAKeyPair = kpGen.generateKeyPair();
        X509Certificate badCACert = generateRootCert(badCAKeyPair, "CN=Bad CA Certificate", 2l);
        badCACertPEMString = toPEM(badCACert);

        // Client
        KeyPair clientKeyPair = kpGen.generateKeyPair();
        X509Certificate clientCert = generateEndEntityCert("CN=client", clientKeyPair.getPublic(), caKeyPair, caCert, 3l);
        X509Certificate badClientCert = generateEndEntityCert("CN=client", clientKeyPair.getPublic(), badCAKeyPair, badCACert, 4l);
        X509Certificate badClientCert2 = generateEndEntityCert("CN=client2", clientKeyPair.getPublic(), caKeyPair, caCert, 5l);
        clientPrivateKeyPEMString = toPEM(clientKeyPair.getPrivate());
        clientCertPEMString = toPEM(clientCert);
        badClientCertPEMString = toPEM(badClientCert);
        badClientCert2PEMString = toPEM(badClientCert2);

        // Server
        KeyPair serverKeyPair = kpGen.generateKeyPair();
        X509Certificate serverCert = generateEndEntityCert("CN=server", serverKeyPair.getPublic(), caKeyPair, caCert, 6);
        serverPrivateKeyPEMString = toPEM(serverKeyPair.getPrivate());
        serverCertPEMString = toPEM(serverCert);
    }
    
    @Test
    public void testMultiplePEMObjectsDecoding() throws IOException, CertificateException {
        CharArrayReader car = new CharArrayReader((serverPrivateKeyPEMString + serverCertPEMString).toCharArray());
        PEMParser pemr = new PEMParser(car);
        ArrayList<Object> v = new ArrayList<Object>();
        Object o = pemr.readObject();
        while (o != null) {
            v.add(o);
            o = pemr.readObject();
        }
        assertEquals(2, v.size());
        assertTrue(v.get(0) instanceof PEMKeyPair);
        assertTrue(v.get(1) instanceof X509CertificateHolder);
        assertNotNull(((PEMKeyPair) v.get(0)).getPrivateKeyInfo().parsePrivateKey());
        assertNotNull(((PEMKeyPair) v.get(0)).getPublicKeyInfo().parsePublicKey());
        assertNotNull(new JcaX509CertificateConverter().setProvider("BC").getCertificate((X509CertificateHolder) v.get(1)).getPublicKey());
    }

    @Ignore
    public static String toPEM(Object o) throws IOException {
        CharArrayWriter caw = new CharArrayWriter();
        JcaPEMWriter pemw = new JcaPEMWriter(caw);
        pemw.writeObject(o);
        pemw.close();
        return caw.toString();
    }

    @Ignore
    public static Object fromPEM(String pemString) throws IOException, CertificateException {
        CharArrayReader car = new CharArrayReader(pemString.toCharArray());
        PEMParser pemr = new PEMParser(car);
        Object o = pemr.readObject();
        if (o instanceof PEMKeyPair) {
            return new JcaPEMKeyConverter().setProvider("BC").getKeyPair((PEMKeyPair) o);
        } else if(o instanceof X509CertificateHolder) {
            return new JcaX509CertificateConverter().setProvider("BC").getCertificate((X509CertificateHolder) o);
        } else {
            throw new IOException();
        }
    }

    @Test
    public void testIt() throws Exception {

        Thread thread = new Thread() {
            public boolean completed;

            class SimpleHandshakeListener implements HandshakeCompletedListener {

                String ident;

                public SimpleHandshakeListener(String ident) {
                    this.ident = ident;
                }

                @Override
                public void handshakeCompleted(HandshakeCompletedEvent event) {
                    try {
                        X509Certificate cert = (X509Certificate) event.getPeerCertificates()[0];
                        String peer = cert.getSubjectDN().getName();
                        System.out.println(ident + ": Request from " + peer);
                        completed = true;
                    } catch (SSLPeerUnverifiedException pue) {
                        System.out.println(ident + ": Peer unverified");
                    }
                }
            }

            @Override
            public void run() {
                System.out.println("server-thread started");
                //System.setProperty("javax.net.debug","ssl hanshake");
                try {
                    SSLServerSocketFactory ssf
                            = getSSLServerSocketFactory(serverPrivateKeyPEMString, serverCertPEMString, new String[]{caCertPEMString});
                    final SSLServerSocket serverSocket
                            = (SSLServerSocket) ssf.createServerSocket(SERVER_PORT);
                    serverSocket.setNeedClientAuth(true);
                    //serverSocket.setWantClientAuth(true);
                    boolean done = false;
                    while (!done) {
                        String ident = "server-thread";
                        System.out.println(ident + ": waiting for connection ...");
                        SSLSocket socket = (SSLSocket) serverSocket.accept();
                        try {
                            completed = false;
                            System.out.println(ident + ": accept() returned");
                            HandshakeCompletedListener hcl = new SimpleHandshakeListener(ident);
                            //socket.setUseClientMode(false);
                            socket.addHandshakeCompletedListener(hcl);
                            socket.setSoTimeout(5000);
                            //System.out.println(ident + ": starting handshake");
                            //socket.startHandshake();
                            System.out.println(ident + ": handshake done " + completed);
                            SSLSession sslSession = socket.getSession();
                            String cipherSuite = sslSession.getCipherSuite();
                            System.out.println(ident + ": session cipher suite is " + cipherSuite);
                            System.out.println(ident + ": handshake done " + completed);
                            if (!socket.getSession().getPeerPrincipal().toString().equals("CN=client")) {
                                throw new Exception("unknown remote principal " + socket.getSession().getPeerPrincipal().toString());
                            }
                            System.out.println(ident + ": handshake done " + completed);
                            System.out.println(ident + ": local principal is " + socket.getSession().getLocalPrincipal().toString());
                            System.out.println(ident + ": remote principal is " + socket.getSession().getPeerPrincipal().toString());
                            System.out.println(ident + ": handshake done " + completed);
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            System.out.println(ident + ": starting while loop");
                            Scanner scanner = new Scanner(socket.getInputStream());
                            while (!done && scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                System.out.println(ident + ": received from client: " + line);
                                if (line.equals("close")) {
                                    done = true;
                                    continue;
                                }
                                bw.write("ok: " + line + "\n");
                                bw.flush();
                                // Make sure the handshake completed event has been fired by now.
                                // Interestingly, nothing else than actual I/O on the SSL socket seems to
                                // force that event...
                                // Update: nope. in rare cases even at this point that event has not fired....
//                                if(!completed) {
//                                    throw new Exception(ident+": handshake not yet completed");
//                                }
                            }
                            System.out.println(ident + ": closes connection");
                        } catch (Exception e) {
                            System.out.println(ident + ": killed: " + e.toString());
                            e.printStackTrace(System.out);
                        }
                        socket.close();
                    }
                    System.out.println("server-thread: is terminating");

                } catch (Exception e) {
                    System.out.println("server-thread: killed: " + e.toString());
                    e.printStackTrace(System.out);
                }
            }
        };
        thread.start();

        try {
            SSLSocketFactory sf
                    = getSSLSocketFactory(clientPrivateKeyPEMString, clientCertPEMString, new String[]{caCertPEMString});
            SSLSocket clientSocket = waitForSSLSocket(sf);
            clientSocket.setUseClientMode(true);
            clientSocket.setSoTimeout(5000);
            //System.out.println("client: starting handshake");
            //clientSocket.startHandshake();
            //SSLSession session = clientSocket.getSession();
            //System.out.println("client: handshake done");
            System.out.println("client: " + clientSocket.getSession().getPeerPrincipal().toString());
            for (java.security.cert.Certificate cert : clientSocket.getSession().getPeerCertificates()) {
                X509Certificate c = (X509Certificate) cert;
                System.out.println("client: peer cert notAfter value = " + c.getNotAfter());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            bw.write("Hello!\n");
            bw.flush();
            assertEquals("ok: Hello!", br.readLine());
            clientSocket.close();
        } catch (Exception e) {
            System.err.println("client: " + e.getMessage());
            throw e;
        }

        try {
            SSLSocketFactory sf
                    = getSSLSocketFactory(clientPrivateKeyPEMString, badClientCertPEMString, new String[]{caCertPEMString});
            SSLSocket clientSocket = (SSLSocket) sf.createSocket(InetAddress.getLocalHost().getHostAddress(), SERVER_PORT);
            clientSocket.setUseClientMode(true);
            clientSocket.setSoTimeout(5000);
            System.out.println("client: starting handshake");
            clientSocket.startHandshake();
            //SSLSession session = clientSocket.getSession();
            System.out.println("client: 1");
            System.out.println("client: " + clientSocket.getSession().getPeerPrincipal().toString());
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            System.out.println("client: 1");
            bw.write("Hello!\n");
            System.out.println("client: 1");
            bw.flush();
            System.out.println("client: 1");
            assertEquals("ok: Hello!", br.readLine());
            System.out.println("client requests connection close");
            bw.write("close\n");
            bw.flush();
            clientSocket.close();
            fail("bad client cert accepted by server");
        } catch (Exception e) {
            System.out.println("client: " + e.getMessage());
        }

        try {
            SSLSocketFactory sf
                    = getSSLSocketFactory(clientPrivateKeyPEMString, badClientCert2PEMString, new String[]{caCertPEMString});
            SSLSocket clientSocket = (SSLSocket) sf.createSocket(InetAddress.getLocalHost().getHostAddress(), SERVER_PORT);
            clientSocket.setUseClientMode(true);
            clientSocket.setSoTimeout(5000);
            System.out.println("client: starting handshake");
            clientSocket.startHandshake();
            //SSLSession session = clientSocket.getSession();
            System.out.println("client: " + clientSocket.getSession().getPeerPrincipal().toString());
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            bw.write("Hello!\n");
            bw.flush();
            assertEquals(null, br.readLine());
            System.out.println("client requests connection close");
            bw.write("close\n");
            bw.flush();
            fail("oups");
            clientSocket.close();
        } catch (Exception e) {
        }

        try {
            SSLSocketFactory sf
                    = getSSLSocketFactory(clientPrivateKeyPEMString, clientCertPEMString, new String[]{caCertPEMString});
            SSLSocket clientSocket = (SSLSocket) sf.createSocket(InetAddress.getLocalHost().getHostAddress(), SERVER_PORT);
            clientSocket.setUseClientMode(true);
            clientSocket.setSoTimeout(5000);
            System.out.println("client: starting handshake");
            clientSocket.startHandshake();
            System.out.println("client: " + clientSocket.getSession().getPeerPrincipal().toString());
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            bw.write("Hello!\n");
            bw.flush();
            assertEquals("ok: Hello!", br.readLine());
            bw.write("close\n");
            bw.flush();
            clientSocket.close();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        System.out.println("waiting for server thread to finish ...");
        thread.join();
    }

    protected SSLServerSocketFactory getSSLServerSocketFactory(String keyPEMString, String certPEMString, String[] caCerts)
            throws IOException, GeneralSecurityException {
        TrustManager[] tms = getTrustManagers(caCerts);
        KeyManager[] kms = getKeyManagers(keyPEMString, certPEMString);
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(kms, tms, null);
        SSLServerSocketFactory ssf = context.getServerSocketFactory();
        return ssf;
    }

    protected SSLSocketFactory getSSLSocketFactory(String keyPEMString, String certPEMString, String[] caCerts)
            throws IOException, GeneralSecurityException {
        TrustManager[] tms = getTrustManagers(caCerts);
        KeyManager[] kms = getKeyManagers(keyPEMString, certPEMString);
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(kms, tms, null);
        SSLSocketFactory ssf = context.getSocketFactory();
        return ssf;
    }

    protected KeyManager[] getKeyManagers(String keyPEMString, String certPEMString)
            throws IOException, GeneralSecurityException {
        String alg = KeyManagerFactory.getDefaultAlgorithm();
        System.out.println("default kmf alg = " + alg);
        alg = "SunX509";
        KeyManagerFactory kmFact = KeyManagerFactory.getInstance(alg);
        KeyStore ks = KeyStore.getInstance("bks", "BC");
        ks.load(null, null);
        ks.setCertificateEntry("mycert", (java.security.cert.Certificate) fromPEM(certPEMString));
        ks.setKeyEntry("mykey", ((KeyPair) fromPEM(keyPEMString)).getPrivate(), "".toCharArray(), new java.security.cert.Certificate[]{(java.security.cert.Certificate) fromPEM(certPEMString)});
        kmFact.init(ks, "".toCharArray());
        KeyManager[] kms = kmFact.getKeyManagers();
        return kms;
    }

    protected TrustManager[] getTrustManagers(String[] caCerts)
            throws IOException, GeneralSecurityException {

        String alg = TrustManagerFactory.getDefaultAlgorithm();
        System.out.println("default tmf alg = " + alg);
        alg = "SunX509";
        TrustManagerFactory tmFact = TrustManagerFactory.getInstance(alg);
        KeyStore ks = KeyStore.getInstance("bks", "BC");
        ks.load(null, null);
        for (int i = 0; i < caCerts.length; i++) {
            ks.setCertificateEntry("cacert" + i, (java.security.cert.Certificate) fromPEM(caCerts[i]));
        }
        tmFact.init(ks);
        TrustManager[] tms = tmFact.getTrustManagers();

//        TrustManager[] trustAllCerts = new TrustManager[]{
//            new X509TrustManager() {
//
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//
//                public void checkClientTrusted(
//                        java.security.cert.X509Certificate[] certs, String authType) {
//                }
//
//                public void checkServerTrusted(
//                        java.security.cert.X509Certificate[] certs, String authType) {
//                }
//            }
//        };
        return tms;
    }

    // http://www.programcreek.com/java-api-examples/index.php?source_dir=neo4j-java-driver-master/driver/src/test/java/org/neo4j/driver/util/CertificateToolTest.java
    @Ignore
    public static X509Certificate generateRootCert(KeyPair caKeyPair, String dn, long serial)
            throws Exception {
        SubjectPublicKeyInfo pubkeyInfo = SubjectPublicKeyInfo.getInstance(caKeyPair.getPublic().getEncoded());
        X509v1CertificateBuilder builder = new X509v1CertificateBuilder(
                new X500Name(dn),
                BigInteger.valueOf(serial),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + VALIDITY_PERIOD),
                new X500Name(dn),
                pubkeyInfo);
        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1WithRSAEncryption");
        AlgorithmIdentifier digestAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
        AsymmetricKeyParameter privateKey = PrivateKeyFactory.createKey(caKeyPair.getPrivate().getEncoded());
        ContentSigner signer = new BcRSAContentSignerBuilder(sigAlgId, digestAlgId).build(privateKey);
        X509CertificateHolder holder = builder.build(signer);
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
    }

    @Ignore
    public static X509Certificate generateCert(boolean intermediate, String subject, PublicKey intKey,
            KeyPair caKeyPair, X509Certificate caCert, long serial)
            throws Exception {
        SubjectPublicKeyInfo pubkeyInfo = SubjectPublicKeyInfo.getInstance(intKey.getEncoded());
        SubjectPublicKeyInfo caPubkeyInfo = SubjectPublicKeyInfo.getInstance(caKeyPair.getPublic().getEncoded());
        X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
                new X500Name(caCert.getSubjectX500Principal().getName()),
                BigInteger.valueOf(serial),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + VALIDITY_PERIOD),
                new X500Name(subject),
                pubkeyInfo);

        DigestCalculator digCalc = new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
        X509ExtensionUtils x509ExtensionUtils = new X509ExtensionUtils(digCalc);
        builder.addExtension(Extension.authorityKeyIdentifier, false, x509ExtensionUtils.createAuthorityKeyIdentifier(caPubkeyInfo));
        builder.addExtension(Extension.subjectKeyIdentifier, false, x509ExtensionUtils.createSubjectKeyIdentifier(pubkeyInfo));
        if (intermediate) {
            builder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
        } else {
            builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.encipherOnly));
        }

        builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));

//        KeyPurposeId[] EKU = new KeyPurposeId[2];
//        EKU[0] = KeyPurposeId.id_kp_emailProtection;
//        if (intermediate) {
//            EKU[1] = KeyPurposeId.id_kp_serverAuth;
//        } else {
//            EKU[1] = KeyPurposeId.id_kp_clientAuth;
//        }
//        builder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(EKU));

        //Certificate Policies
        PolicyInformation[] certPolicies = new PolicyInformation[2];
        certPolicies[0] = new PolicyInformation(new ASN1ObjectIdentifier("2.16.840.1.101.2.1.11.5"));
        certPolicies[1] = new PolicyInformation(new ASN1ObjectIdentifier("2.16.840.1.101.2.1.11.18"));

        builder.addExtension(Extension.certificatePolicies, false, new CertificatePolicies(certPolicies));

        //Subject Alternative Name
        GeneralName[] genNames = new GeneralName[2];
        genNames[0] = new GeneralName(GeneralName.rfc822Name, new DERIA5String("john.smith@gmail.com"));
        genNames[1] = new GeneralName(GeneralName.directoryName, new X500Name("C=US,O=Cyberdyne,OU=PKI,CN=SecureCA"));

        builder.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(genNames));

        //Authority Information Access
        AccessDescription caIssuers = new AccessDescription(AccessDescription.id_ad_caIssuers,
                new GeneralName(GeneralName.uniformResourceIdentifier, new DERIA5String("http://www.somewebsite.com/ca.cer")));
        AccessDescription ocsp = new AccessDescription(AccessDescription.id_ad_ocsp,
                new GeneralName(GeneralName.uniformResourceIdentifier, new DERIA5String("http://ocsp.somewebsite.com")));

        ASN1EncodableVector aia_ASN = new ASN1EncodableVector();
        aia_ASN.add(caIssuers);
        aia_ASN.add(ocsp);

        builder.addExtension(Extension.authorityInfoAccess, false, new DERSequence(aia_ASN));

        //CRL Distribution Points
        DistributionPointName distPointOne = new DistributionPointName(new GeneralNames(
                new GeneralName(GeneralName.uniformResourceIdentifier, "http://crl.somewebsite.com/master.crl")));
        DistributionPointName distPointTwo = new DistributionPointName(new GeneralNames(
                new GeneralName(GeneralName.uniformResourceIdentifier, "ldap://crl.somewebsite.com/cn%3dSecureCA%2cou%3dPKI%2co%3dCyberdyne%2cc%3dUS?certificaterevocationlist;binary")));

        DistributionPoint[] distPoints = new DistributionPoint[2];
        distPoints[0] = new DistributionPoint(distPointOne, null, null);
        distPoints[1] = new DistributionPoint(distPointTwo, null, null);

        builder.addExtension(Extension.cRLDistributionPoints, false, new CRLDistPoint(distPoints));

        //Content Signer
        ContentSigner sigGen = new JcaContentSignerBuilder("SHA1WithRSAEncryption").setProvider("BC").build(caKeyPair.getPrivate());
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(builder.build(sigGen));
        return certificate;
    }

    @Ignore
    public static X509Certificate generateEndEntityCert(String subject, PublicKey entityKey,
            KeyPair caKeyPair, X509Certificate caCert, long serial)
            throws Exception {
        return generateCert(false, subject, entityKey, caKeyPair, caCert, serial);
    }

    @Ignore
    private SSLSocket waitForSSLSocket(SSLSocketFactory sf) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() < start + CONNECTION_TIMEOUT_MILLIS) {
            try {
                return (SSLSocket) sf.createSocket(InetAddress.getLocalHost().getHostAddress(), SERVER_PORT);
            } catch (IOException ex) {
            }
            Thread.sleep(50L);
        }
        throw new RuntimeException("ssl connect timeout");
    }
}
