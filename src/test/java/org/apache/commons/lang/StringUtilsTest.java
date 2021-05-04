package org.apache.commons.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

public class StringUtilsTest {
    @Test
    public void testSetJoin() {
        Set<Long> set = new HashSet<>();
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(1L);
        list.add(1L);
        set.addAll(list);
        assertEquals("1,1,1", StringUtils.join(list, ","));
        assertEquals("1", StringUtils.join(set, ","));
    }
}
