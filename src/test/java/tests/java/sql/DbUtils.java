package tests.java.sql;

import static org.junit.Assert.assertTrue;

import org.h2.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DbUtils.class);
    
    private final Connection conn;
    private final PreparedStatement psTableCheck;

    public static DbUtils getInstance(File dbFile) throws SQLException, ClassNotFoundException {
        System.setProperty("h2.bindAddress", "localhost");
        Class.forName(Driver.class.getName());
        String url = "jdbc:h2:" + dbFile.getAbsolutePath();
        Connection connection = DriverManager.getConnection(url, "sa", "sa");
        connection.setAutoCommit(true);
        LOG.info(String.format("connected to: " + url));
        return new DbUtils(connection);
    }
    
    public DbUtils(Connection conn) throws SQLException {
        this.conn = conn;
        psTableCheck = conn.prepareStatement(
            "SELECT COUNT(1) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?");
        init();
    }

    private void init() throws SQLException {
    }

    public boolean existsTable(String tableName) throws SQLException {
        psTableCheck.setString(1, tableName);
        try (ResultSet rs = psTableCheck.executeQuery()) {
            assertTrue(rs.next());
            return rs.getInt(1) != 0;
        }
    }
    
    public Connection getConnection() {
        return conn;
    }
}
