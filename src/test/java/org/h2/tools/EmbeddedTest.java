package org.h2.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import org.apache.commons.io.FileUtils;
import org.h2.Driver;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.TimeZone;

/**
 * In this test we demonstrate how to start the embedded h2 management front-end
 * web server for an existing database without starting a browser, so we just
 * have a web server port available when we need the management front-end in
 * production, i.e. to be able to set web-application and other configuration
 * settings without having to use additional configuration files and such.
 *
 * @author jjYBdx4IL
 */
public class EmbeddedTest {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedTest.class);
    private static final File TEST_DIR = Maven.getTempTestDir(EmbeddedTest.class);
    private static final File TEST_DB = new File(TEST_DIR, "h2db");
    private static final String DB_URL = "jdbc:h2:" + TEST_DB.getAbsolutePath();
    private Connection conn = null;

    private static final TimeZone defaultTimeZoneBackup = TimeZone.getDefault();

    @AfterClass
    public static void afterClass() {
        // restore default timezone to avoid introducing interdependencies
        // between the test units...
        // (avoid using compile-on-save in Netbeans... it will save the class
        // using lmod time from
        // the wrong timezone and therefore refuse to update it upon further
        // changes!)
        TimeZone.setDefault(defaultTimeZoneBackup);
    }

    @Before
    public void before() throws Exception {
        FileUtils.cleanDirectory(TEST_DIR);
        // this must be set before loading the h2 driver or any of its classes:
        System.setProperty("h2.bindAddress", "localhost");
        Class.forName(Driver.class.getName());
        conn = DriverManager.getConnection(DB_URL, "sa", "sa");
        conn.setAutoCommit(true);
        LOG.info("connected to: " + DB_URL);
    }

    @After
    public void after() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    public void test() throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT COUNT(1) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?");

        // assert that we have a newly created db
        ps.setString(1, "TEST");
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.first());
        assertEquals(0, rs.getInt(1));

        // init db
        Statement stmt = conn.createStatement();
        // http://www.h2database.com/html/grammar.html#create_table
        assertFalse(stmt.execute("CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255))"));
        stmt.close();

        stmt = conn.createStatement();
        assertFalse(stmt.execute("insert into TEST (ID,NAME) VALUES (1, 'one')"));
        stmt.close();

        stmt = conn.createStatement();
        rs = stmt.executeQuery("SELECT ID, NAME FROM TEST");
        assertTrue(rs.next());
        assertEquals(1, rs.getInt(1));
        assertEquals("one", rs.getString(2));
        stmt.close();

        // verify table in information schema
        ps.setString(1, "TEST");
        rs = ps.executeQuery();
        assertTrue(rs.first());
        assertEquals(1, rs.getInt(1));
    }

    // for simple and direct (UTC) times, use (set|get)Timestamp because (set|get)Date adjust for default timezone...
    @Test
    public void testTime() throws SQLException {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));

        // init db
        Statement stmt = conn.createStatement();
        // http://www.h2database.com/html/grammar.html#create_table
        assertFalse(stmt.execute("CREATE TABLE TEST2(ID INT PRIMARY KEY, TIME TIMESTAMP)"));
        stmt.close();

        // SQL DATE is ONLY a date, not a time
        Date testDate = new Date(0);
        Timestamp testTimestamp = new Timestamp(0);

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO TEST2 (ID, TIME) VALUES (?, ?)")) {
            // beware! setDate uses default timezone!
            ps.setInt(1, 1);
            ps.setDate(2, testDate);
            ps.execute();
            // setTimestamp doesn't
            ps.setInt(1, 2);
            ps.setTimestamp(2, testTimestamp);
            ps.execute();
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT TIME FROM TEST2 WHERE ID = ?")) {
            ps.setInt(1, 1);
            try (ResultSet rs = ps.executeQuery()) {
                assertNotNull(rs);
                assertTrue(rs.next());
                Timestamp storedTimestamp = rs.getTimestamp("TIME");
                assertNotNull(storedTimestamp);
                assertNotEquals(testTimestamp, storedTimestamp);
                assertEquals(testTimestamp.getTime() - 3600 * 1000, storedTimestamp.getTime());
                Date storedDate = rs.getDate(1);
                assertNotNull(storedDate);
                assertNotEquals(testDate, storedDate);
                assertEquals(testDate.getTime() - 3600 * 1000, storedDate.getTime());
            }
            ps.setInt(1, 2);
            try (ResultSet rs = ps.executeQuery()) {
                assertNotNull(rs);
                assertTrue(rs.next());
                Timestamp storedTimestamp = rs.getTimestamp("TIME");
                assertNotNull(storedTimestamp);
                assertEquals(testTimestamp, storedTimestamp);
                assertEquals(testTimestamp.getTime(), storedTimestamp.getTime());
                Date storedDate = rs.getDate(1);
                assertNotNull(storedDate);
                assertNotEquals(testDate, storedDate);
                assertEquals(testDate.getTime() - 3600 * 1000, storedDate.getTime());
            }
        }
    }
    
    @Test
    public void testSmallDateTime() throws SQLException {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));

        // init db
        Statement stmt = conn.createStatement();
        // http://www.h2database.com/html/grammar.html#create_table
        assertFalse(stmt.execute("CREATE TABLE TEST3(ID INT PRIMARY KEY, TIME SMALLDATETIME)"));
        stmt.close();

        // SQL DATE is ONLY a date, not a time
        Timestamp testTimestamp = new Timestamp(500);

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO TEST3 (ID, TIME) VALUES (?, ?)")) {
            // setTimestamp doesn't
            ps.setInt(1, 1);
            ps.setTimestamp(2, testTimestamp);
            ps.execute();
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT TIME FROM TEST3 WHERE ID = ?")) {
            ps.setInt(1, 1);
            try (ResultSet rs = ps.executeQuery()) {
                assertNotNull(rs);
                assertTrue(rs.next());
                Timestamp storedTimestamp = rs.getTimestamp("TIME");
                assertNotNull(storedTimestamp);
                // sub second value get rounded to the closest second:
                assertEquals(testTimestamp.getTime(), storedTimestamp.getTime() - 500);
            }
        }
    }
}
