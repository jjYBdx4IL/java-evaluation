package org.junit;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.*;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class BeforeInParentClass1Test extends BeforeInParentClassBase {

    @Test
    public void test1() {
        assertEquals(0, variableFromParentClass);
        variableFromParentClass++;
    }
    
    @Test
    public void test2() {
        assertEquals(0, variableFromParentClass);
        variableFromParentClass++;
    }

    @Test
    public void test3() {
        assertEquals(0, variableFromParentClass);
        variableFromParentClass++;
    }

    @Test
    public void singularTest() {
        assertEquals(0, staticVariableFromParentClass);
        staticVariableFromParentClass++;
    }

    @Test
    public void testStaticList() {
        assertEquals(0, staticList.size());
        staticList.add("1");
        staticList.add("2");
        staticList.add("3");
    }
}
