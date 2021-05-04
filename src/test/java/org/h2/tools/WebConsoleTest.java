package org.h2.tools;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.env.Surefire;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.h2.Driver;
import org.h2.engine.Constants;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In this test we demonstrate how to start the embedded h2 managment frontend web server for an existing database
 * without starting a browserr, so we just have a web server port available when we need the managment frontend in
 * production, ie. to be able to set webapp and other configuration settings without having to use additional
 * configuration files and such.
 *
 * @author jjYBdx4IL
 */
public class WebConsoleTest {

    private static final Logger LOG = LoggerFactory.getLogger(WebConsoleTest.class); 
    
    private static final File TEST_DIR = Maven.getTempTestDir(WebConsoleTest.class);
    private static final File TEST_DB = new File(TEST_DIR, "h2db");
    private static final File H2_WEB_PROPS_FILE = new File(TEST_DIR, Constants.SERVER_PROPERTIES_NAME);
    private static final String DB_URL = "jdbc:h2:" + TEST_DB.getAbsolutePath();
    private Connection conn = null;

    @Before
    public void before() throws Exception {
        FileUtils.cleanDirectory(TEST_DIR);
        // this must be set before loading the h2 driver or any of its classes:
        System.setProperty("h2.bindAddress", "localhost");
        Class.forName(Driver.class.getName());
        conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);
    }

    @After
    public void after() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close(); 
        }
    }
    
    @Test
    public void test() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());
        
        // first, make sure we have an existing database
        Statement stmt = conn.createStatement();
        // http://www.h2database.com/html/grammar.html#create_table
        assertFalse(stmt.execute("CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255))"));
        stmt.close();
        
        stmt = conn.createStatement();
        assertFalse(stmt.execute("insert into TEST (ID,NAME) VALUES (1, 'one')"));
        stmt.close();
        
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT ID, NAME FROM TEST");
        assertTrue(rs.next());
        assertEquals(1, rs.getInt(1));
        assertEquals("one", rs.getString(2));
        stmt.close();
        
        // inject connection settings into frontend config
        Properties webServerProps = new Properties();
        webServerProps.put("0", String.format(Locale.ROOT, "Generic H2 (Embedded)|org.h2.Driver|jdbc\\:h2\\:%s",
                TEST_DB.getAbsolutePath().replace("\\", "\\\\").replace(":", "\\:")));
        try (OutputStream os = new FileOutputStream(H2_WEB_PROPS_FILE)) {
            webServerProps.store(os, "");
        }
         
        Server server = new Server();
        server.runTool("-web", "-webPort", "8083", "-ifExists", "-baseDir", TEST_DIR.getAbsolutePath(),
                "-properties", TEST_DIR.getAbsolutePath());

        URL url = new URL("http://localhost:8083");
        Desktop.getDesktop().browse(url.toURI());
        
        LOG.info("end the test by shutting down the web server from the web server console!");
        
        while(isRunning(url)) {
            Thread.sleep(1000L); 
        }
        server.stop();
    }
    
    private boolean isRunning(URL url) {
        try {
            IOUtils.toString(url, "UTF-8");
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

}
