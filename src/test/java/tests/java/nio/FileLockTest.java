package tests.java.nio;

import com.github.jjYBdx4IL.utils.env.Maven;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;

public class FileLockTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(FileLockTest.class);

    @Test(expected = OverlappingFileLockException.class)
    public void testLockExclusiveTwice() throws IOException {
        File file1 = new File(TEMP_DIR, "file");
        File file2 = new File(TEMP_DIR, "file");
        // make sure the lock file exists, but also don't overwrite an existing
        // one (needs to be atomic for obvious reasons)
        file1.createNewFile();
        try (FileOutputStream out = new FileOutputStream(file1, true)) {
            FileLock lock = out.getChannel().lock();
            // NOT appending does not circumvent the file lock:
            try (FileOutputStream out2 = new FileOutputStream(file2, false)) {
                FileLock lock2 = out2.getChannel().lock();
                try {
                    OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                    writer.append("123");
                } finally {
                    lock2.release();
                }
            } finally {
                lock.release();
            }
        }
    }

    @Test
    public void testLockExclusive() throws IOException {
        File file = new File(TEMP_DIR, "file");
        // make sure the lock file exists, but also don't overwrite an existing
        // one (needs to be atomic for obvious reasons)
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file, true);
        try {
            FileLock lock = out.getChannel().lock();
            try {
                @SuppressWarnings("resource")
                OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                writer.append("123");
            } finally {
                lock.release();
            }
        } finally {
            out.close();
        }
    }

    @Test
    public void testLockShared() throws IOException {
        File file = new File(TEMP_DIR, "file");
        // make sure the lock file exists, but also don't overwrite an existing
        // one (needs to be atomic for obvious reasons)
        file.createNewFile();
        FileInputStream in = new FileInputStream(file);
        try {
            FileLock lock = in.getChannel().lock(0, Long.MAX_VALUE, true);
            try {
                @SuppressWarnings({ "unused", "resource" })
                Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            } finally {
                lock.release();
            }
        } finally {
            in.close();
        }
    }

}
