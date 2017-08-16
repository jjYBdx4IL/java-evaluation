package tests.java.sql;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * HSQLDB performance tests.
 * 
 * Observations:
 *
 * 1.) NOT setting it results in sequential insert rates of 20k inserts per
 * second on SATA disks using a single connection or thread!
 *
 * 2.) parallel inserts with forced syncs (ie. outside transactions with ACID
 * props) do not scale with the number of parallel threads although sync ops
 * could merge all outstanding commit requests at once...
 *
 * => HSQLDB is not too useful as a persistent disk database engine.
 *
 * @link http://hsqldb.org/doc/1.8/guide/apb.html
 */
public class HSQLDBTest extends DBPerformanceTestBase {

    @Before
    public void setUp() throws IOException, SQLException {
        FileUtils.deleteDirectory(new File(getDBDirectory()));
        Connection conn = getConn();
        update(conn,
                "CREATE CACHED TABLE sample_table ( id INTEGER IDENTITY, str_col VARCHAR(256), num_col INTEGER)");
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File(getDBDirectory()));
    }

    @Ignore
    @Override
    protected Connection getConn() throws SQLException {
        // http://hsqldb.org/doc/2.0/guide/dbproperties-chapt.html

        // in-memory DB:
        //return DriverManager.getConnection("jdbc:hsqldb:mem:db_file", "sa", "");
        // on-disk DB:
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:" + getDBDirectory() + ";hsqldb.write_delay_millis=0", "sa", "");
        return conn;
    }
    
    @Test
    public void test() throws SQLException {
        Connection conn = getConn();

        for (int i = 0; i < 10; i++) {
            update(conn,
                    "INSERT INTO sample_table(str_col,num_col) VALUES('Ford', 100)");
            update(conn,
                    "INSERT INTO sample_table(str_col,num_col) VALUES('Toyota', 200)");
            update(conn,
                    "INSERT INTO sample_table(str_col,num_col) VALUES('Honda', 300)");
            update(conn,
                    "INSERT INTO sample_table(str_col,num_col) VALUES('GM', 400)");
        }
        query(conn, "SELECT * FROM sample_table WHERE num_col < 250");
        shutdown(conn);
    }

    @Ignore
    @Test
    public void test2() throws SQLException, InterruptedException {
        testParallelRandomUpdates(10, 10);
    }

    @Override
    protected void shutdown(Connection conn) throws SQLException {

        Statement st = conn.createStatement();

        // db writes out to files and performs clean shuts down
        // otherwise there will be an unclean shutdown
        // when program ends
        st.execute("SHUTDOWN");
        conn.close();    // if there are no other open connection
    }
}
