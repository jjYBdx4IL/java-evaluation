package org.apache.commons.collections;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class MapUtilsTest {

    @Test
    public void testGetLongDefault() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("1", "1");
        assertEquals(1L, MapUtils.getLong(map, "1", -1L).longValue());
        
        map.put("2", "1a");
        assertEquals(1L, MapUtils.getLong(map, "2", -1L).longValue());
        
        map.put("3", "a1");
        assertEquals(-1L, MapUtils.getLong(map, "3", -1L).longValue());
        
        map.put("4", null);
        assertEquals(-1L, MapUtils.getLong(map, "4", -1L).longValue());
        
        map.put("5", "");
        assertEquals(-1L, MapUtils.getLong(map, "5", -1L).longValue());
        
        map.put("6", new BigInteger("1"));
        assertEquals(1L, MapUtils.getLong(map, "6", -1L).longValue());
        
        map.put("7", 1d);
        assertEquals(1L, MapUtils.getLong(map, "7", -1L).longValue());
        
        assertEquals(-1L, MapUtils.getLong(map, "92839", -1L).longValue());
    }
    
}
