package tests.java.net;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;

public class DNSCacheTest {

    @Ignore
    @Test
    public void test1() throws InterruptedException {
        System.out.println(System.getProperties());
//        System.setProperty("networkaddress.cache.ttl", "1");
        while (true) {
            try {
                InetAddress[] inetAddrs = InetAddress.getAllByName("dnscachetest");

                for (InetAddress inetAddress : inetAddrs) {
                    System.out.println(inetAddress);
                }
            } catch (UnknownHostException ex) {
                Logger.getLogger(DNSCacheTest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            Thread.sleep(1000L);
        }
    }
}
