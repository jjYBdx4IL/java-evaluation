/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.nio;

import com.github.jjYBdx4IL.utils.env.Surefire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FSyncTest {

    public static final File tmpDir = Surefire.getTempDirForClassRT(FSyncTest.class);

    public static final byte[] TESTDATA = "abc".getBytes(Charset.forName("ASCII"));
    public static final byte[] TESTDATA2 = "DEF".getBytes(Charset.forName("ASCII"));

    // strace -f mvn -Dtest=tests.java.nio.FSyncTest#testOldIOFSync  surefire:test 2>&1 | grep "write.*abc\|write.*DEF\|sync"
    @Test
    public void testOldIOFSync() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        try (FileOutputStream s = new FileOutputStream(new File(tmpDir, "testOldIOFSync.dat"), false)) {
            s.write("abc".getBytes("ASCII"));
            s.flush(); // flush does *NOT* trigger a Linux fsync call (which syncs metadata and file data to a local disk)
            s.getFD().sync(); // this triggers the fsync call (flush seems to be implicit in this case, ie
                //  the write is done before the fsync call even when omitting the flush command above)
        }
        // there is no fsync call at all if one comments out the sync() call above, not even when the system/JVM
        // is closing the file.
    }

    // strace -f mvn -Dtest=tests.java.nio.FSyncTest#testNewIOFSync  surefire:test 2>&1 | grep "write.*abc\|write.*DEF\|sync"
    @Test
    public void testNewIOFSync() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        try (FileOutputStream s = new FileOutputStream(new File(tmpDir, "testNewIOFSync.dat"), false);
                FileChannel c = s.getChannel()) {
            ByteBuffer bb = ByteBuffer.allocate(100);
            bb.put(TESTDATA);
            bb.flip();
            c.write(bb);
            c.force(false); // this calls fdatasync after writing the contents out to disk

            bb.flip();
            bb.put(TESTDATA2);
            bb.flip();
            c.write(bb);
            c.force(true); // this calls fsync after writing the contents out to disk

            bb.flip();
            bb.put(TESTDATA);
            bb.flip();
            c.write(bb);
        }
        // no fsync/fdatasync is called when closing the file
    }
}
