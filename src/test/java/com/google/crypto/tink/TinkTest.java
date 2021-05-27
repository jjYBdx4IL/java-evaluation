package com.google.crypto.tink;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AesGcmKeyManager;
import com.google.crypto.tink.subtle.Hex;
import com.privatejgoodies.common.base.SystemUtils;
import com.sun.jna.platform.win32.Advapi32Util;
import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;

// https://github.com/google/tink/blob/master/docs/JAVA-HOWTO.md (not up-to-date)
public class TinkTest {

    private static final Logger LOG = LoggerFactory.getLogger(TinkTest.class);

    public static final long WARMUP_MS = 200L;
    public static final long MEASUREMENT_MS = 300L;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testSymmetric() throws Exception {
        File keysetFile = new File(folder.getRoot(), "keyset.json");

        // create new key for symmetric encryption/decryption
        AeadConfig.register();
        KeysetHandle keysetHandle2 = KeysetHandle.generateNew(AesGcmKeyManager.aes256GcmTemplate());

        // store it to a file ...
        CleartextKeysetHandle.write(keysetHandle2, JsonKeysetWriter.withFile(keysetFile));
        LOG.info(FileUtils.readFileToString(keysetFile, StandardCharsets.UTF_8));

        // ... and re-load it
        KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(keysetFile));

        // encrypt ("v1" - the AAD - is not a secret, rather sort of a protocol identifier that's protected (verified)
        // by the cryptographic TAG appended to the ciphertext)
        // https://crypto.stackexchange.com/questions/35727/does-aad-make-gcm-encryption-more-secure
        // https://bugs.openjdk.java.net/browse/JDK-8062828
        Aead daead = keysetHandle.getPrimitive(Aead.class);
        byte[] ciphertext = daead.encrypt("msg".getBytes(), "v1".getBytes());
        LOG.info("ciphertext: " + Hex.encode(ciphertext));

        // second encryption yields different result
        byte[] ciphertext2 = daead.encrypt("msg".getBytes(), "v1".getBytes());
        LOG.info("ciphertext: " + Hex.encode(ciphertext2));
        assertNotEquals(Hex.encode(ciphertext), Hex.encode(ciphertext2));

        // decrypt
        byte[] decrypted = daead.decrypt(ciphertext, "v1".getBytes());
        assertEquals("msg", new String(decrypted, StandardCharsets.UTF_8));

        // try to decrypt with a non-matching AAD
        try {
            daead.decrypt(ciphertext, "v2".getBytes());
            fail();
        } catch (GeneralSecurityException ex) {
        }

        // try to decrypt with a different key
        KeysetHandle keysetHandle3 = KeysetHandle.generateNew(AesGcmKeyManager.aes256GcmTemplate());
        Aead daead3 = keysetHandle3.getPrimitive(Aead.class);
        try {
            daead3.decrypt(ciphertext, "v1".getBytes());
            fail();
        } catch (GeneralSecurityException ex) {
        }

        int blackhole = 0;

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < WARMUP_MS) {
            // warm up the CPU
        }

        // test encryption performance
        long n = 0;
        byte[] msg = "msghadlföhalkdshfalksdjfhalksdjfhasdlökfj".getBytes();
        byte[] aad = "v1".getBytes();
        start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < MEASUREMENT_MS) {
            ciphertext = daead.encrypt(msg, aad);
            blackhole += ciphertext[20];
            msg[20] ^= ciphertext[30];
            n++;
        }
        float msPerExec = (System.currentTimeMillis() - start) / (float) n;
        int encsPerSec = (int) (1000 / msPerExec);
        LOG.info(String.format("%d encryptions/sec (%d)", encsPerSec, n));

        // test decryption performance
        n = 0;
        start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < MEASUREMENT_MS) {
            ciphertext = daead.encrypt(msg, aad);
            msg[20] ^= ciphertext[30];
            decrypted = daead.decrypt(ciphertext, aad);
            blackhole += decrypted[20];
            n++;
        }
        long msSpentByEncryption = (long) (n * msPerExec); // estimated by result from previous encryption benchmark
                                                           // result
        long msSpentByDecryption = (System.currentTimeMillis() - start) - msSpentByEncryption;
        msPerExec = msSpentByDecryption / (float) n;
        int decsPerSec = (int) (1000 / msPerExec);
        LOG.info(String.format("%d decryptions/sec (%d)", decsPerSec, n));
        LOG.info("java.version: " + System.getProperty("java.version"));
        LOG.info("java.vendor: " + System.getProperty("java.vendor"));
        LOG.info("os.name: " + System.getProperty("os.name"));
        LOG.info("os.version: " + System.getProperty("os.version"));
        LOG.info("os.arch: " + System.getProperty("os.arch"));
        logCpuInfo();
        
//        21:41:36 [main] INFO TinkTest - 159252 encryptions/sec (796261)
//        21:41:41 [main] INFO TinkTest - 174939 decryptions/sec (416881)
//        21:41:41 [main] INFO TinkTest - java.version: 16.0.1
//        21:41:41 [main] INFO TinkTest - java.vendor: Azul Systems, Inc.
//        21:41:41 [main] INFO TinkTest - os.name: Windows 10
//        21:41:41 [main] INFO TinkTest - os.version: 10.0
//        21:41:41 [main] INFO TinkTest - os.arch: amd64
//        21:41:41 [main] INFO TinkTest - AMD Ryzen 5 5600X 6-Core Processor
//        21:41:41 [main] INFO TinkTest - AMD64 Family 25 Model 33 Stepping 0

        // connect blackhole where the JVM can't possibly track
        FileUtils.writeStringToFile(new File(folder.getRoot(), "bh"), "" + blackhole, StandardCharsets.UTF_8);
    }

    public static void logCpuInfo() {
        if (SystemUtils.IS_OS_WINDOWS) {
            LOG.info(Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE,
                    "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\\", "ProcessorNameString"));
            LOG.info(Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE,
                    "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\\", "Identifier"));
        }
    }
}
