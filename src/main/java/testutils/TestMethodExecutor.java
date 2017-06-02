package testutils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class TestMethodExecutor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TestMethodExecutor.class);
    private static final long waitMillis = 500L;

    private volatile MethodRef methodRef = null;
    private volatile boolean running = true;

    private long lastExecTimeMillis = 0;
    private MethodRef lastExecMethodRef = null;

    public void setTestMethodRef(MethodRef newMethodRef) {
        methodRef = newMethodRef;
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void run() {
        LOG.info("started");

        while (running) {
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
                    wait(waitMillis);
                }
            } catch (Exception ex) {
                LOG.error("", ex);
            }
        }
    }

    private void runIt() throws Exception {
        LOG.info("running " + lastExecMethodRef);
        final ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        final URLClassLoader cl = new URLClassLoader(new URL[]{}, origClassLoader);
        try {
            Thread.currentThread().setContextClassLoader(cl);
            Class<?> classRef = cl.loadClass(lastExecMethodRef.getClassName());
            Method method = classRef.getMethod(lastExecMethodRef.getMethodName());
            Object instance = classRef.newInstance();
            method.invoke(instance);
        } finally {
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }
    }

}
