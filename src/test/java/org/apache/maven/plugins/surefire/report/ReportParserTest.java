//@formatter:off
package org.apache.maven.plugins.surefire.report;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testgroup.IgnoreFailure;

import java.io.IOException;

// this file is used to generate a well-defined surefire report
@Category(IgnoreFailure.class)
public class ReportParserTest {

    @Test
    public void test() {
    }

    @Ignore
    @Test // ignored
    public void testIgnore() {
    }
    
    @Test // ignored
    public void testAssumeFailed() {
        assumeTrue(false);
    }
    
    @Test(expected = IOException.class) // test failure
    public void testExpectedFailed() throws IOException {
    }
    
    @Test(expected = IOException.class)
    public void testExpected() throws IOException {
        throw new IOException("test expected exception message");
    }
    
    @Test // test failure
    public void testFail() {
        fail("fail message");
    }
    
    @Test // test error
    public void testIOException() throws IOException {
        throw new IOException("test exception message");
    }
    
    @Test // test failure
    public void testAssertionError() {
        assertTrue(false);
    }
}
