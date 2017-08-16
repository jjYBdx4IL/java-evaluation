/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.lang;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class StacktraceTest {

    private static final Logger log = LoggerFactory.getLogger(StacktraceTest.class);

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
        }
    }
}
