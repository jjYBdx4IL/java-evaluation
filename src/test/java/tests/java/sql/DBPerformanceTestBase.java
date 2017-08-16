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
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class DBPerformanceTestBase {
    protected abstract Connection getConn() throws SQLException;
    protected abstract void shutdown(Connection conn) throws SQLException;
    
    private static final Logger LOG = Logger.getLogger(DBPerformanceTestBase.class);
    
    protected static String getDBDirectory() {
        return System.getProperty("java.io.tmpdir", System.getProperty("basedir", System.getProperty("user.dir"))) + File.separator + "testdb.instance" + File.separator;
    }

    public double testParallelRandomUpdates(int nThreads, int nUpdatesPerThread) throws SQLException, InterruptedException {
        return testParallelRandomUpdates(nThreads, nUpdatesPerThread, 1000000, true);
    }
    
    public double testParallelRandomUpdates(int nThreads, int nUpdatesPerThread, int nExistingRows, boolean autoId) throws SQLException, InterruptedException {
        PreparedStatement insertPS;
        Connection conn = getConn();
        Random r = new Random(0L);

        final int nRowsTotal = nExistingRows;
        int nUpdates = nThreads * nUpdatesPerThread;
        final int _nUpdatesPerThread = nUpdatesPerThread;
        System.out.println("creating " + nRowsTotal + " rows");
        conn.setAutoCommit(false);
        long start = System.currentTimeMillis();
        if(autoId) {
            insertPS = conn.prepareStatement("INSERT INTO sample_table(str_col,num_col) VALUES('Some not too short string used to waste a lot of space...', 1)");
            for (int i = 0; i < nRowsTotal; i++) {
                insertPS.executeUpdate();
                if(i%10000 == 0) {
                    conn.commit();
                    System.out.println(i);
                }
            }
        } else {
            insertPS = conn.prepareStatement("INSERT INTO sample_table(id,str_col,num_col) VALUES(?,'Some not too short string used to waste a lot of space...', 1)");
            for (int i = 0; i < nRowsTotal; i++) {
                insertPS.setInt(1, i+1);
                insertPS.executeUpdate();
                if(i%10000 == 0) {
                    conn.commit();
                    System.out.println(i);
                }
            }
        }
        conn.commit();
        
        double insertsPerSecond = 1000.0 * (double) nRowsTotal / (double) ( System.currentTimeMillis() - start );
        System.out.printf("%.1f inserts per second", insertsPerSecond);
        System.out.println();

        insertPS.close();
        
        conn.setAutoCommit(true);
        
        System.out.println("updating " + nUpdates + " random rows using " + nThreads + " threads in parallel");
        start = System.currentTimeMillis();
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int j = 0; j < nThreads; j++) {
            final long randSeed = r.nextLong();
            final int newIDValue = j + 100;
            Thread t = new Thread() {

                @Override
                public void run() {
                    try {
                        Connection conn = getConn();
                        conn.setAutoCommit(true);
                        Random r = new Random(randSeed);
                        PreparedStatement updatePS = conn.prepareStatement("UPDATE sample_table SET num_col = ? WHERE id = ?");
                        for (int i = 0; i < _nUpdatesPerThread; i++) {
                            updatePS.setInt(1, newIDValue);
                            updatePS.setInt(2, r.nextInt(nRowsTotal));
                            updatePS.executeUpdate();
                        }
                    }
                    catch (Exception ex) {
                    }
                }
            };
            threads.add(t);
            t.start();
        }
        for (int j = 0; j < nThreads; j++) {
            Thread t = threads.get(j);
            t.join();
        }
        shutdown(conn);
        double updatesPerSecond = (double) 1000.0 * (double) nUpdates / (double) ( System.currentTimeMillis() - start );
        System.out.printf("%.1f random updates per second", updatesPerSecond);
        System.out.println();
        return updatesPerSecond;
    }

    public double testParallelInserts(int nThreads, int nInsertsPerThread) throws SQLException, InterruptedException {
        Connection conn = getConn();

        int nInserts = nThreads * nInsertsPerThread;
        final int _nUpdatesPerThread = nInsertsPerThread;
        System.out.println("inserting " + nInserts + " rows using " + nThreads + " threads in parallel");
        long start = System.currentTimeMillis();
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int j = 0; j < nThreads; j++) {
            final int newIDValue = j + 100;
            Thread t = new Thread() {

                @Override
                public void run() {
                    try {
                        Connection conn = getConn();
                        conn.setAutoCommit(true);
                        PreparedStatement insertPS = conn.prepareStatement("INSERT INTO sample_table(str_col,num_col) VALUES('Some not too short string used to waste a lot of space...', ?)");
                        for (int i = 0; i < _nUpdatesPerThread; i++) {
                            insertPS.setInt(1, newIDValue);
                            insertPS.executeUpdate();
                        }
                    }
                    catch (Exception ex) {
                    }
                }
            };
            threads.add(t);
            t.start();
        }
        for (int j = 0; j < nThreads; j++) {
            Thread t = threads.get(j);
            t.join();
        }
        shutdown(conn);
        double insertsPerSecond = 1000.0 * (double) nInserts / (double) ( System.currentTimeMillis() - start );
        System.out.printf("%.1f inserts per second", insertsPerSecond);
        System.out.println();
        return insertsPerSecond;
    }

    //use for SQL command SELECT
    public synchronized void query(Connection conn, String expression) throws SQLException {

        Statement st;
        ResultSet rs;

        st = conn.createStatement();         // statement objects can be reused with

        LOG.log(Level.TRACE, "update(): "+expression);
        
        // repeated calls to execute but we
        // choose to make a new one each time
        rs = st.executeQuery(expression);    // run the query

        // do something with the result set.
        ResultSetUtils.dump(rs);
        st.close();    // NOTE!! if you close a statement the associated ResultSet is

        // closed too
        // so you should copy the contents to some other object.
        // the result set is invalidated also  if you recycle an Statement
        // and try to execute some other query before the result set has been
        // completely examined.
    }

    //use for SQL commands CREATE, DROP, INSERT and UPDATE
    public void update(Connection conn, String expression) throws SQLException {

        Statement st;

        st = conn.createStatement();    // statements
        
        if(LOG.isTraceEnabled()) {
            LOG.log(Level.TRACE, "update(): "+expression);
        }
        
        int i = st.executeUpdate(expression);    // run the query

        if (i == -1) {
            LOG.log(Level.ERROR, "update failed for: "+expression);
        }

        st.close();
    }    // void update()
    
    public static void staticUpdate(Connection conn, String expression) throws SQLException {

        Statement st;

        st = conn.createStatement();    // statements
        
        LOG.log(Level.TRACE, "update(): "+expression);
        
        int i = st.executeUpdate(expression);    // run the query

        if (i == -1) {
            LOG.log(Level.ERROR, "update failed for: "+expression);
        }

        st.close();
    }    // void update()
}
