/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class StacktraceTest {

    public class One {

        public void overridden() {
        }
    }

    public class Two extends One {

        @Override
        public void overridden() {
            throw new RuntimeException();
        }
    }

    @Test
    public void testSubTypeDisplay() {
        try {
            new Two().overridden();
            fail();
        } catch (Exception ex) {
            assertTrue(ex.getStackTrace()[0].getClassName().endsWith("Two"));
            assertEquals(Two.class.getName(), ex.getStackTrace()[0].getClassName());
        }
    }
    
    @Test
    public void testThreadGetStackTrace() {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[1];
        assertEquals(StacktraceTest.class.getName(), ste.getClassName());
        assertEquals("testThreadGetStackTrace", ste.getMethodName());
    }
}
