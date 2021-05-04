/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.apache.commons.compress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.junit4.Screenshot;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CpioArchiveTest {

    private static File workDir = Screenshot.getMavenScreenshotOutputDir(CpioArchiveTest.class);
    private static final Logger log = LoggerFactory.getLogger(CpioArchiveTest.class);
    private static File dataFile = new File(workDir, "data");
    private static final String testContent = "123";

    @Before
    public void before() throws IOException {
        try (OutputStream os = new FileOutputStream(dataFile)) {
            IOUtils.write(testContent, os, "UTF-8");
        }
    }

    /*
$ cpio --version
cpio (GNU cpio) 2.11
Copyright (C) 2010 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.

Written by Phil Nelson, David MacKenzie, John Oleynick,
and Sergey Poznyakoff.

    echo data | cpio -H newc -o > data.cpiotool.newc
     */
    @Test
    public void compareToCpioTool() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("data.cpiotool.newc")) {
            dumpArchive(is);
        }
    }

    private void dumpArchive(InputStream is) throws IOException {
        try (CpioArchiveInputStream cpio = new CpioArchiveInputStream(is)) {
            CpioArchiveEntry ae = null;
            ae = (CpioArchiveEntry) cpio.getNextEntry();
            while (ae != null) {
                log.info(String.format("format: %d", ae.getFormat()));
                ae = (CpioArchiveEntry) cpio.getNextEntry();
            }
        }
    }

    @Test
    public void testNoCRC() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CpioArchiveOutputStream cpio = new CpioArchiveOutputStream(baos, CpioArchiveOutputStream.FORMAT_OLD_ASCII)) {
            CpioArchiveEntry ae = new CpioArchiveEntry(CpioArchiveEntry.FORMAT_OLD_ASCII, dataFile, "ks.cfg");
            cpio.putArchiveEntry(ae);
            try (InputStream is = new FileInputStream(dataFile)) {
                cpio.write(IOUtils.toByteArray(is));
            }
            cpio.closeArchiveEntry();
        }

        log.info(baos.toString("UTF-8"));
    }

    @Test
    public void testNewCRC() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CpioArchiveOutputStream cpio = new CpioArchiveOutputStream(baos, CpioArchiveOutputStream.FORMAT_NEW_CRC)) {
            CpioArchiveEntry ae = new CpioArchiveEntry(CpioArchiveEntry.FORMAT_NEW_CRC, dataFile, "ks.cfg");
            cpio.putArchiveEntry(ae);
            try (InputStream is = new FileInputStream(dataFile)) {
                byte[] data = IOUtils.toByteArray(is);
                cpio.write(data);
                ae.setChksum(computeChksum(data));
            }
            cpio.closeArchiveEntry();
        }

        log.info(baos.toString("UTF-8"));
    }

    public static long computeChksum(byte[] buf) {
        long crc = 0;
        for (int pos = 0; pos < buf.length; pos++) {
            crc += buf[pos] & 0xFF;
        }
        return crc;
    }

}
