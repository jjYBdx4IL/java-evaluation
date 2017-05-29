package tests.java.util;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ListTest {
    @Test
    public void test1() {
        List<Long> ll = new ArrayList<Long>();
        ll.add(0L);
        ll.add(1L);
        ll.add(2L);
        assertEquals(3,ll.size());
        assertEquals("0,1,2", StringUtils.join(ll, ","));
    }
}
