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
import org.apache.commons.io.FileUtils;
import org.junit.*;

public class DerbyTest extends DBPerformanceTestBase {

    @Ignore
    @Override
    protected Connection getConn() throws SQLException {
        return DriverManager.getConnection("jdbc:derby:"+getDBDirectory()+";create=true");
    }

    @Ignore
    @Override
    protected void shutdown(Connection conn) throws SQLException {
        //DriverManager.getConnection("jdbc:derby:"+getDBDirectory()+";shutdown=true");
    }
    
    @SuppressWarnings("deprecation")
    @BeforeClass
    public static void classSetUp() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
    }
    
    @Before
    public void setUp() throws SQLException, IOException {
        FileUtils.deleteDirectory(new File(getDBDirectory()));
        Connection conn = getConn();
        update(conn,
//                "CREATE TABLE sample_table ( id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), str_col VARCHAR(256), num_col INTEGER)");
                "CREATE TABLE sample_table ( id INTEGER, str_col VARCHAR(256), num_col INTEGER)");
        update(conn,
                "CREATE INDEX sample_table_idx ON sample_table(id)");
    }
    
    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File(getDBDirectory()));
    }

    /**
     * Results: merging write requests works quite well unless one forces Derby
     * to use a generated identity when doing inserts. In that case Derby seems
     * to synchronize the id generation to disk for every row and parallelity
     * does not increase the insert rate at all...
     * @throws SQLException
     * @throws InterruptedException 
     */
    @Ignore
    @Test
    public void test1() throws SQLException, InterruptedException {
//   old-fashioned, rotating disk:
//        9990000
//        4731.3 inserts per second
//        updating 100000 random rows using 100 threads in parallel
//        114.3 random updates per second
        testParallelRandomUpdates(100, 1000, 10000000, false);
        //testParallelInserts(100, 100);
    }
}
