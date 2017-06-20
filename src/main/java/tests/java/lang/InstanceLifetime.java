package tests.java.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class InstanceLifetime {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceLifetime.class);

    private final CountDownLatch finalizeLatch;

    static {
        LOG.debug("static init");
    }

    public InstanceLifetime(CountDownLatch finalizeLatch, boolean throwException) {
        LOG.debug("constructor");
        this.finalizeLatch = finalizeLatch;
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        LOG.debug("finalize");
        finalizeLatch.countDown();
        super.finalize();
    }

}
