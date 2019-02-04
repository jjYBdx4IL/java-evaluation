package org.junit;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

// http://kentbeck.github.com/junit/javadoc/latest/org/junit/rules/TestWatcher.html
import java.util.HashMap;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import testgroup.RequiresIsolatedVM;

import static org.junit.runner.JUnitCore.runClasses;

@Category(RequiresIsolatedVM.class)
public class TestWatcherTest {

    public static class TestWatcherTestProper {
        private static Map<String, Throwable> protocol = new HashMap<>();
        
        @Rule
        public TestRule testWatcher = new TestWatcher2(protocol);

        @AfterClass
        public static void afterClass() {
        }

        @Test
        public void fails() {
            Assert.fail();
        }

        @Test
        public void succeeds() {
        }

        @Test
        public void testError() {
            Assert.assertTrue(false);
        }

        @Test
        public void throwsException() {
            throw new RuntimeException("test msg");
        }
    }

    @Test
    public void testme() {
        runClasses(TestWatcherTestProper.class);
        Assert.assertTrue(TestWatcherTestProper.protocol.containsKey("fails-failed"));
        Assert.assertTrue(TestWatcherTestProper.protocol.containsKey("succeeds-succeeded"));
        Assert.assertTrue(TestWatcherTestProper.protocol.containsKey("testError-failed"));
        Assert.assertTrue(TestWatcherTestProper.protocol.containsKey("throwsException-failed"));
        Assert.assertTrue(TestWatcherTestProper.protocol.get("fails-failed") instanceof AssertionError);
        Assert.assertTrue(TestWatcherTestProper.protocol.get("succeeds-failed") == null);
        Assert.assertTrue(TestWatcherTestProper.protocol.get("testError-failed") instanceof AssertionError);
        Assert.assertTrue(TestWatcherTestProper.protocol.get("throwsException-failed") instanceof RuntimeException);
    }
}
