package tests.java.lang;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class InstanceLifetimeTest {

    @Test
    public void testFinalizeCall() throws InterruptedException {
        CountDownLatch finalizeLatch = new CountDownLatch(1);
        try {
            InstanceLifetime t1 = new InstanceLifetime(finalizeLatch, false);
        } catch (Exception ex) {}
        while (!finalizeLatch.await(1, TimeUnit.MICROSECONDS)) {
            StringBuilder sb = new StringBuilder(102400);
        }
    }
    
    @Test
    public void testFinalizeCallAfterConstructorFailure() throws InterruptedException {
        CountDownLatch finalizeLatch = new CountDownLatch(1);
        try {
            InstanceLifetime t1 = new InstanceLifetime(finalizeLatch, true);
            fail();
        } catch (Exception ex) {}
        while (!finalizeLatch.await(1, TimeUnit.MICROSECONDS)) {
            StringBuilder sb = new StringBuilder(102400);
        }
    }
    
}
