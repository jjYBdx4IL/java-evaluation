package org.apache.maven.plugins.surefire.report;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.maven.plugin.surefire.log.api.NullConsoleLogger;
import org.apache.maven.reporting.MavenReportException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testgroup.IT;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

// mvn surefire:test failsafe:integration-test@default-test -Dtest="ReportParser*"
@Category(IT.class) // we need the surefire report for ReportParserTest
public class ReportParserIT {

    SurefireReportParser report = new SurefireReportParser(singletonList(getTestDir()), Locale.getDefault(),
        new NullConsoleLogger());

    @Test
    public void test() throws MavenReportException {
        ReportTestSuite report = getReport();
        assertNotNull("test report not found", report);
        assertEquals(1, report.getNumberOfErrors());
        assertEquals(3, report.getNumberOfFailures());
        assertEquals(0, report.getNumberOfFlakes());
        assertEquals(2, report.getNumberOfSkipped());
        assertEquals(8, report.getNumberOfTests());
        
        Map<String, ReportTestCase> cases = report.getTestCases().stream().collect(
            Collectors.toMap(ReportTestCase::getName, v -> v));
        
//@formatter:off
//        @Test
//        public void test() {
//        }
//@formatter:on
        ReportTestCase rtc = cases.get("test");
        assertEquals("test", rtc.getName());
        assertEquals(true, rtc.isSuccessful());
        assertEquals(false, rtc.hasSkipped());
        assertEquals(false, rtc.hasFailure());
        assertEquals(false, rtc.hasError());
        
//@formatter:off        
//        @Ignore
//        @Test // ignored
//        public void testIgnore() {
//        }
//@formatter:on        
        rtc = cases.get("testIgnore");
        assertEquals(false, rtc.isSuccessful());
        assertEquals(true, rtc.hasSkipped());
        assertEquals(false, rtc.hasFailure());
        assertEquals(false, rtc.hasError());
        
//@formatter:off        
//        @Test // ignored
//        public void testAssumeFailed() {
//            assumeTrue(false);
//        }
//@formatter:on
        rtc = cases.get("testAssumeFailed");
        assertEquals(false, rtc.isSuccessful());
        assertEquals(true, rtc.hasSkipped());
        assertEquals(false, rtc.hasFailure());
        assertEquals(false, rtc.hasError());
  
//@formatter:off
//        @Test(expected = IOException.class) // test failure
//        public void testExpectedFailed() throws IOException {
//        }
//@formatter:on
        rtc = cases.get("testExpectedFailed");
        assertEquals(false, rtc.isSuccessful());
        assertEquals(false, rtc.hasSkipped());
        assertEquals(true, rtc.hasFailure());
        assertEquals(false, rtc.hasError());
        assertEquals("java.lang.AssertionError", rtc.getFailureType());
        assertEquals("Expected exception: java.io.IOException", rtc.getFailureMessage());
        assertEquals("", rtc.getFailureErrorLine());
        assertEquals("java.lang.AssertionError: Expected exception: java.io.IOException", rtc.getFailureDetail());

//@formatter:off
//        @Test(expected = IOException.class)
//        public void testExpected() throws IOException {
//            throw new IOException("test expected exception message");
//        }
//@formatter:on
        rtc = cases.get("testExpected");
        assertEquals(true, rtc.isSuccessful());
        assertEquals(false, rtc.hasSkipped());
        assertEquals(false, rtc.hasFailure());
        assertEquals(false, rtc.hasError());
        assertEquals(null, rtc.getFailureType());
        assertEquals(null, rtc.getFailureMessage());
        assertEquals(null, rtc.getFailureErrorLine());
        assertEquals(null, rtc.getFailureDetail());
        
//@formatter:off
//        @Test // test failure
//        public void testFail() {
//            fail("fail message");
//        }
//@formatter:on
        rtc = cases.get("testFail");
        assertEquals(false, rtc.isSuccessful());
        assertEquals(false, rtc.hasSkipped());
        assertEquals(true, rtc.hasFailure());
        assertEquals(false, rtc.hasError());
        assertEquals("java.lang.AssertionError", rtc.getFailureType());
        assertEquals("fail message", rtc.getFailureMessage());
        assertEquals("44", rtc.getFailureErrorLine());
        assertEquals("java.lang.AssertionError: fail message\n" + 
            "\tat org.apache.maven.plugins.surefire.report.ReportParserTest.testFail(ReportParserTest.java:44)",
            rtc.getFailureDetail().replace("\r", ""));
        
//@formatter:off        
//        @Test // test error
//        public void testIOException() throws IOException {
//            throw new IOException("test exception message");
//        }
//@formatter:on
        rtc = cases.get("testIOException");
        assertEquals(false, rtc.isSuccessful());
        assertEquals(false, rtc.hasSkipped());
        assertEquals(false, rtc.hasFailure());
        assertEquals(true, rtc.hasError());
        assertEquals("java.io.IOException", rtc.getFailureType());
        assertEquals("test exception message", rtc.getFailureMessage());
        assertEquals("49", rtc.getFailureErrorLine());
        assertEquals("java.io.IOException: test exception message\n" + 
            "\tat org.apache.maven.plugins.surefire.report.ReportParserTest.testIOException(ReportParserTest.java:49)",
            rtc.getFailureDetail().replace("\r", ""));
    
//@formatter:off        
//        @Test // test failure
//        public void testAssertionError() {
//            assertTrue(false);
//        }
//@formatter:on
        rtc = cases.get("testAssertionError");
        assertEquals(false, rtc.isSuccessful());
        assertEquals(false, rtc.hasSkipped());
        assertEquals(true, rtc.hasFailure());
        assertEquals(false, rtc.hasError());
        assertEquals("java.lang.AssertionError", rtc.getFailureType());
        assertEquals(null, rtc.getFailureMessage());
        assertEquals("54", rtc.getFailureErrorLine());
        assertEquals("java.lang.AssertionError\n" + 
            "\tat org.apache.maven.plugins.surefire.report.ReportParserTest.testAssertionError(ReportParserTest.java:54)",
            rtc.getFailureDetail().replace("\r", ""));
    }
    
    private File getTestDir() {
        return Paths.get("target/surefire-reports").toFile();
    }

    private ReportTestSuite getReport() throws MavenReportException {
        List<ReportTestSuite> suites = report.parseXMLReportFiles();
        for (ReportTestSuite suite : suites) {
            if (suite.getFullClassName().equals(ReportParserTest.class.getName())) {
                return suite;
            }
        }
        return null;
    }
}
