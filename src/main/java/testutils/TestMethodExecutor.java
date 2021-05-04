package testutils;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class TestMethodExecutor extends RunListener implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TestMethodExecutor.class);
    private static final long POLL_MILLIS = 10L;

    private volatile MethodRef methodRef = null;
    private volatile boolean running = true;

    private long lastExecTimeMillis = 0;
    private MethodRef lastExecMethodRef = null;
    private final String moduleUriPrefix;

    public TestMethodExecutor(String moduleUriPrefix) {
        this.moduleUriPrefix = moduleUriPrefix;
    }

    public void setTestMethodRef(MethodRef newMethodRef) {
        methodRef = newMethodRef;
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void run() {
        LOG.info("started, moduleUriPrefix = " + moduleUriPrefix);

        while (running) {
            LOG.trace("loop");
            try {
                if (methodRef != null) {
                    long lmod = new File(new URI(methodRef.getResourceUri())).lastModified();
                    if (methodRef.equals(lastExecMethodRef) && lmod != lastExecTimeMillis || !methodRef.equals(lastExecMethodRef)) {
                        lastExecTimeMillis = lmod;
                        lastExecMethodRef = methodRef;
                        runIt();
                    }
                }

                synchronized (this) {
                    wait(POLL_MILLIS);
                }
            } catch (Exception | ExceptionInInitializerError ex) {
                LOG.error("", ex);
            }
        }
    }

    private void runIt() throws Exception {
        LOG.trace("running " + lastExecMethodRef);
        final ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        final URLClassLoader cl = new URLClassLoader(new URL[]{new URL(moduleUriPrefix)}, origClassLoader);
        try {
            Thread.currentThread().setContextClassLoader(cl);
            Class<?> classRef = cl.loadClass(lastExecMethodRef.getClassName());

            BlockJUnit4ClassRunner r = new BlockJUnit4ClassRunner(classRef);
            Description method = Description.createTestDescription(classRef, lastExecMethodRef.getMethodName());
            r.filter(Filter.matchMethodDescription(method));
            RunNotifier runNotifier = new RunNotifier();
            runNotifier.addListener(this);
            r.run(runNotifier);
        } finally {
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        LOG.info("testAssumptionFailure() " + failure);
        super.testAssumptionFailure(failure);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        LOG.info("testFinished() " + description);
        super.testFinished(description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        LOG.info("testFailure() " + failure.getTrace());
        super.testFailure(failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        LOG.info("testIgnored() " + description);
        super.testIgnored(description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        LOG.info("testRunFinished() " + result);
        super.testRunFinished(result);
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        LOG.info("testRunStarted() " + description);
        super.testRunStarted(description);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        LOG.info("testStarted() " + description);
        super.testStarted(description);
    }
}
