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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class WatchServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchServiceTest.class);
    private static final CountDownLatch WATCHER_REGISTERED = new CountDownLatch(1);
    private File TEST_FILE;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void before() {
        TEST_FILE = new File(folder.getRoot(), "test.file");
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
                    Path TEMP_DIR = folder.getRoot().toPath();
                    WatchService watchService = TEMP_DIR.getFileSystem().newWatchService();
                    TEMP_DIR.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.OVERFLOW);
                    WATCHER_REGISTERED.countDown();
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
                            if (TEMP_DIR.relativize(TEST_FILE.toPath()).toFile().equals(p.toFile())) {
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
        assertTrue(WATCHER_REGISTERED.await(10, TimeUnit.SECONDS));
        TEST_FILE.createNewFile();
        t.join();
        assertFalse(TEST_FILE.exists());
        LOG.debug("directory watch thread detected the file and deleted it");
    }
}
