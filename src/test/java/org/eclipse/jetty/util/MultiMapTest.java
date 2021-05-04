package org.eclipse.jetty.util;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.eclipse.jetty.util.MultiMap;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author
 */
public class MultiMapTest {
    @Test
    public void testMultiMapComparison() {
        MultiMap<String> p1 = new MultiMap<String>();
        MultiMap<String> p2 = new MultiMap<String>();
        
        p1.add("a", "1");
        p1.add("a", "2");
        p2.add("a", "1");
        p2.add("a", "2");
        
        assertTrue(p1.equals(p2));
        
        p1 = new MultiMap<String>();
        p2 = new MultiMap<String>();
        
        p1.add("a", "1");
        p1.add("a", "2");
        p2.add("a", "2");
        p2.add("a", "1");
        
        // order matters
        assertFalse(p1.equals(p2));
        
        assertEquals(null, p1.getString("not-existing"));
    }


    /**
     * Uri uri=Uri.parse(url_string);
     * uri.getQueryParameter("para1");
     */
//    @Test
//    public void testUrlDecodeMultiMapComparison() {
//        MultiMap<String> p1 = new MultiMap<String>();
//
//        UrlEncoded.decodeTo("a=1&b=2", p1, "UTF-8");
//        assertEquals("1", p1.getString("a"));
//        assertEquals("2", p1.getString("b"));
//
//        UrlEncoded.decodeTo("a=1&b=2", p1, "UTF-8");
//        assertEquals("1,1", p1.getString("a"));
//        assertEquals("2,2", p1.getString("b"));
//
//        MultiMap<String> p2 = new MultiMap<String>();
//        UrlEncoded.decodeTo("a=1&b=2&a=4", p2, "UTF-8");
//        assertEquals("1,4", p2.getString("a"));
//        assertEquals("2", p2.getString("b"));
//
//        assertFalse(p1.equals(p2));
//
//        p2 = new MultiMap<String>();
//        UrlEncoded.decodeTo("a=1&b=2&a=1&b=2", p2, "UTF-8");
//        assertTrue(p1.equals(p2));
//    }
}
