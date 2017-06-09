/*
 * Copyright (C) 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package testutils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rapid Development Runner 2.
 * 
 * Unfinished.
 *
 * @author jjYBdx4IL
 */
public class RDRunner2 extends BlockJUnit4ClassRunner {

    private static final Logger LOG = LoggerFactory.getLogger(RDRunner2.class);
    private final static long POLL_MILLIS = 300l;

    public RDRunner2(Class<?> klass) throws InitializationError {
        super(klass);
        Thread.currentThread().getContextClassLoader();
    }

    public static String getMethodName() {
        String test = System.getProperty("test", "");
        if (test.indexOf('#') == -1) {
            throw new IllegalArgumentException("no method name in test property");
        }
        return test.substring(test.indexOf('#') + 1);
    }

    @Override
    public void run(final RunNotifier notifier) {
        LOG.debug("run()");
        EachTestNotifier testNotifier = new EachTestNotifier(notifier, getDescription());
        getChildren();
        try {
            setNewClassLoader();
            Class<?> klass = getTestClass().getJavaClass();
            BlockJUnit4ClassRunner r = new BlockJUnit4ClassRunner(
                    Thread.currentThread().getContextClassLoader().loadClass(klass.getName()));

            // Description method = Description.createTestDescription(klass,
            // getMethodName());
            // r.filter(Filter.matchMethodDescription(method));
            r.run(notifier);
            // Statement statement = classBlock(notifier);
            // statement.evaluate();

            Statement statement = classBlock(notifier);
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            testNotifier.fireTestIgnored();
        } catch (StoppedByUserException e) {
            throw e;
        } catch (Throwable e) {
            testNotifier.addFailure(e);
        } finally {
        }
    }

    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
        LOG.debug("runChild()");
        Description description = describeChild(method);
        if (method.getAnnotation(Ignore.class) != null) {
            notifier.fireTestIgnored(description);
        } else {
            runTestUnit(methodBlock(method), description, notifier);
        }
    }

    @Override
    protected Object createTest() throws Exception {
        LOG.debug("createTest()");
        return super.createTest();
    }

    /**
     * Runs a {@link Statement} that represents a leaf (aka atomic) test.
     * 
     * @param statement
     * @param description
     * @param notifier
     */
    protected final void runTestUnit(Statement statement, Description description, RunNotifier notifier) {
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        AssumptionViolatedException ave = null;
        Throwable failure = null;
        LOG.debug("runTestUnit()");
        eachNotifier.fireTestStarted();
        try {
            for (;;) {
                try {
                    statement.evaluate();
                } catch (AssumptionViolatedException e) {
                    ave = e;
                } catch (Throwable e) {
                    failure = e;
                    try {
                        Thread.sleep(POLL_MILLIS);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        } finally {
            if (ave != null) {
                eachNotifier.addFailedAssumption(ave);
            }
            if (failure != null) {
                eachNotifier.addFailure(failure);
            }
            eachNotifier.fireTestFinished();
        }
    }

    protected void setNewClassLoader() {
        LOG.debug("setNewClassLoader()");
        URLClassLoader cl = new URLClassLoader(new URL[] {});
        Thread.currentThread().setContextClassLoader(cl);
    }

    public static long getLastModified(Class<?> classRef) {
        ClassLoader cl = classRef.getClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        String classResourceFileName = classRef.getName().replace('.', '/') + ".class";
        try {
            return new File(cl.getResource(classResourceFileName).toURI()).lastModified();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
