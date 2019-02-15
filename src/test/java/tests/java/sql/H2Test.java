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

public class H2Test extends DBPerformanceTestBase {

    @Ignore
    @Override
    protected Connection getConn() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:"+getDBDirectory()+"db;create=true");
    }

    @Ignore
    @Override
    protected void shutdown(Connection conn) throws SQLException {
        //DriverManager.getConnection("jdbc:h2:"+getDBDirectory()+";shutdown=true");
        update(conn, "SHUTDOWN");
    }
    
    @SuppressWarnings("deprecation")
    @BeforeClass
    public static void classSetUp() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class.forName("org.h2.Driver").newInstance();
    }
    
    @Before
    public void setUp() throws SQLException, IOException {
        FileUtils.deleteDirectory(new File(getDBDirectory()));
        Connection conn = getConn();
        update(conn,
                "CREATE TABLE sample_table ( id INTEGER IDENTITY, str_col VARCHAR(256), num_col INTEGER)");
    }
    
    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File(getDBDirectory()));
    }
    
    @Ignore
    @Test
    public void test1() throws SQLException, InterruptedException {
        testParallelRandomUpdates(1, 1000);
//        testParallelRandomUpdates(10, 100);
    }
}
