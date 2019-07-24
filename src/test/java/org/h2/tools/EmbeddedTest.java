package org.h2.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.apache.commons.io.FileUtils;
import org.h2.Driver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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
    
}
