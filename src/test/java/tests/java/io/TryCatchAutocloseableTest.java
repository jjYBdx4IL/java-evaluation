package tests.java.io;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class TryCatchAutocloseableTest {

    private static final Logger LOG = LoggerFactory.getLogger(TryCatchAutocloseableTest.class);
    
    @Test
    public void test() throws IOException {
        
        AtomicInteger closeCounter1 = new AtomicInteger(0);
        AtomicInteger closeCounter2 = new AtomicInteger(0);
        
        try (
            TInputStream is = new TInputStream("".getBytes(), closeCounter1);
            TInputStream is2 = new TInputStream("".getBytes(), closeCounter2)) {
            assertEquals(0, closeCounter1.longValue());
            assertEquals(0, closeCounter2.longValue());
        }
        
        assertEquals(1, closeCounter1.longValue());
        assertEquals(1, closeCounter2.longValue());
    }
    
    public static class TInputStream extends ByteArrayInputStream {

        private AtomicInteger closeCounter;
        
        public TInputStream(byte[] buf, AtomicInteger cnt) {
            super(buf);
            closeCounter = cnt;
        }

        @Override
        public void close() throws IOException {
            LOG.info("close()");
            closeCounter.incrementAndGet();
            super.close();
        }
        
    }
}
