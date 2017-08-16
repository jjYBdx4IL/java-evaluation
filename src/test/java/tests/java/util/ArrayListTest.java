package tests.java.util;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.ArrayList;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * https://docs.oracle.com/javase/tutorial/collections/interfaces/collection.html
 * @author Github jjYBdx4IL Projects
 */
public class ArrayListTest {

    private static final ArrayList<Integer> listOne = new ArrayList<>();

    @BeforeClass
    public static void beforeClass() {
        listOne.add(1);
        listOne.add(2);
        listOne.add(3);
    }

    @Test
    public void testStreamSum() {
        assertEquals(6, listOne.stream()
                .mapToInt(x -> x).sum());
    }

    @Test
    public void testStreamFilter() {
        assertEquals(4, listOne.stream()
                .mapToInt(x -> x)
                .filter(x -> x != 2)
                .sum());
    }

    @Test
    public void testParallelStreamSum() {
        assertEquals(6, listOne.parallelStream()
                .mapToInt(x -> x).sum());
    }

}
