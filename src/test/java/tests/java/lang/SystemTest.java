package tests.java.lang;

import org.junit.Test;

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
public class SystemTest {

    @Test
    public void testErrPrint() {
        byte b = 0x31; // "1"
        int c = ((int)b) & 0xff;
        System.err.println((char)c);
    }
}
