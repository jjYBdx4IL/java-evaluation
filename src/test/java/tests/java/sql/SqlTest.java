package tests.java.sql;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(SqlTest.class);
    private static final File DB_FILE = new File(TEMP_DIR, "h2db");
    private Connection conn = null;
    private DbUtils dbUtils = null;

    @Before
    public void setUp() throws SQLException, IOException, ClassNotFoundException {
        FileUtils.cleanDirectory(TEMP_DIR);
        dbUtils = DbUtils.getInstance(DB_FILE);
        conn = dbUtils.getConnection();
    }

    @After
    public void tearDown() throws IOException, SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("SHUTDOWN");
    }

    @Test
    public void testPreparedStatementBatch() throws SQLException, InterruptedException {
        conn.createStatement().execute("CREATE TABLE sample_table ( id INTEGER PRIMARY KEY, str_col VARCHAR(256) )");

        PreparedStatement ps = conn.prepareStatement("MERGE INTO sample_table (id, str_col) VALUES (?,?)");
        ps.setInt(1, 1);
        ps.setString(2, "one");
        ps.addBatch();

        ps.addBatch();

        ps.setInt(1, 2);
        ps.setString(2, "two");
        ps.addBatch();

        int[] updateCounts = ps.executeBatch();
        assertArrayEquals(new int[] { 1, 1, 1 }, updateCounts);

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(1) FROM sample_table");
        rs.next();
        assertEquals(2, rs.getInt(1));
    }
}
