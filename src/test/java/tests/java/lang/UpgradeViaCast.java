package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.fail;

import org.junit.Test;

public class UpgradeViaCast {
    class Basic {
        public String prop;
    }
    class BasicUpgrade extends Basic {
    }
    
    @SuppressWarnings("unused")
	@Test
    public void test1() {
        Basic basic = new Basic();
        try {
            BasicUpgrade basicUpgrade = (BasicUpgrade) basic;
            fail();
        } catch(ClassCastException e) {}
    }
}
