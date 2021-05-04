package org.junit;

import java.util.ArrayList;
import java.util.List;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class BeforeInParentClassBase {

    protected int variableFromParentClass = -1;
    protected static int staticVariableFromParentClass = -1;
    protected static final List<String> staticList = new ArrayList<>();

    @Before
    public void beforeTest() {
        variableFromParentClass = 0;
    }

    @BeforeClass
    public static void beforeClass() {
        staticVariableFromParentClass = 0;
        staticList.clear();
    }
}
