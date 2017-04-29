package tests.java.util.zip;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// CHECKSTYLE IGNORE MagicNumber FOR NEXT 1000 LINES
public class ZipTest {

    private static final Logger LOG = LoggerFactory.getLogger(ZipTest.class);
    private static final String TEST_LINE_CONTENT = "this is some test content, you know.\n";
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testZipOutputStream() throws IOException {
        File outZipFile = folder.newFile("test.zip");
        LOG.info("writing to zip file " + outZipFile.getAbsolutePath());
        // CHECKSTYLE IGNORE InnerAssignment FOR NEXT 1 LINE
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outZipFile))) {
            ZipEntry e = new ZipEntry("testentry1");
            zos.putNextEntry(e);
            for (int i = 0; i < 1000; i++) {
                zos.write(TEST_LINE_CONTENT.getBytes());
            }
            zos.closeEntry();
        }
    }

	@Test
    public void testInvididualFileAccess() throws IOException {
        File outZipFile = folder.newFile("test.zip");
        LOG.info("writing to zip file " + outZipFile.getAbsolutePath());
        // CHECKSTYLE IGNORE InnerAssignment FOR NEXT 1 LINE
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outZipFile))) {
            for (int i = 0; i < 1000; i++) {
                ZipEntry e = new ZipEntry("testentry" + i);
                zos.putNextEntry(e);
                zos.write(Integer.toString(i).getBytes("ASCII"));
                zos.closeEntry();
            }
        }
        // CHECKSTYLE IGNORE InnerAssignment FOR NEXT 1 LINE
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(outZipFile))) {
            for (int i = 0; i < 1000; i++) {
                ZipEntry e = zis.getNextEntry();
                if (e.getName().equals("testentry500")) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(zis));
                    assertEquals("500", br.readLine());
                }
            }
        }
    }
}
