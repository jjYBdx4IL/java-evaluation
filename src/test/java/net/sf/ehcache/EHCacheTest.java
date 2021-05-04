package net.sf.ehcache;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * EHCache can NOT be used as a persistent disk cache without commercial enterprise addons.....
 *
 * @author Github jjYBdx4IL Projects
 */

@SuppressWarnings("all")
public class EHCacheTest {

    @Test
    public void testMemCache() throws InterruptedException {
        // initialize the cache
        CacheManager singletonManager = CacheManager.create();
        Cache memoryOnlyCache = new Cache("testCache", 5000, false, false, 1, 10000);
        singletonManager.addCache(memoryOnlyCache);
        Cache cache = singletonManager.getCache("testCache");
        assertNotNull(cache);
        
        // store some test element
        Element e = new Element("a", "b");
        cache.put(e);
        
        // get element
        Element e2 = cache.get("a");
        assertNotNull(e2);
        String s = (String) e2.getValue();
        assertTrue(s.equals("b"));
        
        // check element expiration
        Thread.sleep(1500);
        assertNull("make sure idle time does not override live time", cache.get("a"));
    }
    
    @Test
    public void testNeverExpire() throws InterruptedException {
        // initialize the cache
        CacheManager singletonManager = CacheManager.create();
        Cache memoryOnlyCache = new Cache("testCache2", 5000, false, true, 10, 20);
        singletonManager.addCache(memoryOnlyCache);
        Cache cache = singletonManager.getCache("testCache2");
        assertNotNull(cache);
        
        // store some test element
        Element e = new Element("a", "b");
        cache.put(e);
        
        // get element
        Element e2 = cache.get("a");
        assertNotNull(e2);
        assertEquals(0, e2.getTimeToIdle());
        assertEquals(0, e2.getTimeToLive());
        String s = (String) e2.getValue();
        assertTrue(s.equals("b"));
    }
    
    @Test
    public void testManualExpire() throws InterruptedException {
        // initialize the cache
        CacheManager singletonManager = CacheManager.create();
        Cache memoryOnlyCache = new Cache("testCache3", 5000, false, false, 3600, 3600);
        singletonManager.addCache(memoryOnlyCache);
        Cache cache = singletonManager.getCache("testCache3");
        assertNotNull(cache);
        
        // store some test element
        Element e = new Element("a", "b");
        cache.put(e);
        
        // get element
        Element e2 = cache.get("a");
        assertNotNull(e2);
        assertEquals(3600, e2.getTimeToIdle());
        assertEquals(3600, e2.getTimeToLive());
        String s = (String) e2.getValue();
        assertTrue(s.equals("b"));
        
        Thread.sleep(10);
        long age = System.currentTimeMillis() - e2.getCreationTime();
        assertTrue(5 < age);
        assertTrue(age < 300*1000);
        
        // overwriting the key updates the creation time
        e = new Element("a", "b");
        cache.put(e);
        e2 = cache.get("a");
        age = System.currentTimeMillis() - e2.getCreationTime();
        assertTrue(age < 5);
    }
}
