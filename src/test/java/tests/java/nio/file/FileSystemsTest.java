package tests.java.nio.file;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;

public class FileSystemsTest {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemsTest.class);

    @Test
    public void test() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        Path root = FileSystems.getDefault().getPath("/boot");

        LOG.info(root + ": ");
        try {
            FileStore store = Files.getFileStore(root);
            LOG.info("available=" + nf.format(store.getUsableSpace())
                + ", total=" + nf.format(store.getTotalSpace()));
        } catch (IOException ex) {
            LOG.error("error querying space: ", ex);
        }
    }
}
