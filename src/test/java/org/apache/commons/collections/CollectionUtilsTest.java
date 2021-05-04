package org.apache.commons.collections;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CollectionUtilsTest {

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testImmutability() {
        List<String> aList = new ArrayList<>();
        aList.add("test");
        @SuppressWarnings("unchecked")
		Collection<String> unmodifiableCollection = CollectionUtils.unmodifiableCollection(aList);
        try {
            unmodifiableCollection.add("123");
            fail();
        } catch (UnsupportedOperationException ex) {
        }
        try {
            unmodifiableCollection.remove(0);
            fail();
        } catch (UnsupportedOperationException ex) {
        }
        try {
            unmodifiableCollection.iterator().remove();
            fail();
        } catch (UnsupportedOperationException ex) {
        }
        unmodifiableCollection.toArray()[0] = "213";
        assertEquals("test", unmodifiableCollection.iterator().next());
    }

}
