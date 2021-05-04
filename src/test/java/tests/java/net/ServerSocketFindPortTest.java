/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.net;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Evaluation on how to find a free TCP listener port.
 *
 * @author Github jjYBdx4IL Projects
 */
public class ServerSocketFindPortTest {

    private static final Logger LOG = LoggerFactory.getLogger(ServerSocketFindPortTest.class);

    @Test
    public void testPortZero() throws IOException {
        ServerSocket ss = new ServerSocket(0);
        assertTrue(ss.getLocalPort() > 0);
        LOG.info("" + ss.getLocalPort());
        ss.close();
    }

    @SuppressWarnings("resource")
    @Test
    public void testPortConflict() throws IOException {
        ServerSocket ss = new ServerSocket(0);
        assertTrue(ss.getLocalPort() > 0);
        LOG.info("" + ss.getLocalPort());

        try {
            new ServerSocket(ss.getLocalPort());
            fail();
        } catch (BindException ex) {
        }

        ss.close();
    }
}
