package tests.java.util;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SortTest {

	@Test
	public void testSimpleSort() {
		Map<String,String> map = new HashMap<>();
		map.put("c","654654");
		map.put("a","2654654");
		map.put("b","97843");
		List<String> keys = new ArrayList<>(map.keySet());
		Collections.sort(keys);
		assertEquals("a", keys.get(0));
		assertEquals("b", keys.get(1));
		assertEquals("c", keys.get(2));
	}

	@Test
	public void testComparatorSort() {
		Map<String,String> map = new HashMap<>();
		map.put("c","654654");
		map.put("a","2654654");
		map.put("b","97843");
		List<String> keys = new ArrayList<>(map.keySet());
		Collections.sort(keys, new Comparator<String>() {

            @Override
            public int compare(String arg0, String arg1) {
                return arg1.compareTo(arg0);
            }
        });
		assertEquals("c", keys.get(0));
		assertEquals("b", keys.get(1));
		assertEquals("a", keys.get(2));
	}
}
