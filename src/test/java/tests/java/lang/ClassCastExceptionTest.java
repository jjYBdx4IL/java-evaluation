/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.lang;

import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ClassCastExceptionTest {

    @Test
    public void test() {
        Object o = new String();
        try {
            Integer i = (Integer) o;
            fail();
        } catch (ClassCastException ex) {}
    }

}
