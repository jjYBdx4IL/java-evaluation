package tests.java.nio.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.env.Maven;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class WatchServiceTest {

    public static final File TEMP_DIR = Maven.getTempTestDir(WatchServiceTest.class);
    private static final Logger LOG = LoggerFactory.getLogger(WatchServiceTest.class);
    private static final AtomicBoolean WATCHER_REGISTERED = new AtomicBoolean(false);
    private static final File TEST_FILE = new File(TEMP_DIR, "test.file");

    @Before
    public void cleanupBefore() throws IOException {
        FileUtils.cleanDirectory(TEMP_DIR);
    }

    /**
     * WatchService does *not* work recursively on directories. Each directory must be registered separately.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testWatchService() throws IOException, InterruptedException {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    WatchService watchService = TEMP_DIR.toPath().getFileSystem().newWatchService();
                    TEMP_DIR.toPath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.OVERFLOW);
                    synchronized (WATCHER_REGISTERED) {
                        WATCHER_REGISTERED.set(true);
                        WATCHER_REGISTERED.notifyAll();
                    }
                    // loop forever to watch directory
                    while (true) {
                        WatchKey watchKey;
                        watchKey = watchService.take(); // this call is blocking until events are present

                        // poll for file system events on the WatchKey
                        for (final WatchEvent<?> event : watchKey.pollEvents()) {
                            assertNotNull(event.context());
                            assertTrue(event.context() instanceof Path);
                            Path p = (Path) event.context();
                            LOG.debug(p.toFile().getPath() + " " + event.kind());
                            if (TEMP_DIR.toPath().relativize(TEST_FILE.toPath()).toFile().equals(p.toFile())) {
                                TEST_FILE.delete();
                                return;
                            }
                        }

                        // if the watched directed gets deleted, get out of run method
                        if (!watchKey.reset()) {
                            LOG.error("No longer valid");
                            watchKey.cancel();
                            watchService.close();
                            break;
                        }
                    }
                } catch (InterruptedException ex) {
                    LOG.error("interrupted. Goodbye");
                } catch (IOException ex) {
                    LOG.error("", ex);
                }
            }
        });
        if (TEST_FILE.getParentFile() != null && !TEST_FILE.getParentFile().exists()) {
            TEST_FILE.getParentFile().mkdirs();
        }
        t.start();
        synchronized (WATCHER_REGISTERED) {
            while (!WATCHER_REGISTERED.get()) {
                WATCHER_REGISTERED.wait();
            }
        }
        TEST_FILE.createNewFile();
        t.join();
        assertFalse(TEST_FILE.exists());
        LOG.debug("directory watch thread detected the file and deleted it");
    }
}
