package tests.java.lang.annotation;

import static org.junit.Assert.*;
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
public class AnnotationWithParamsTest {

    @Test
    public void test() throws NoSuchMethodException {
        assertEquals(3, (int) AnnotationWithParams.class.getMethod("retries").getDefaultValue());
        assertEquals(4, (int) AnnotationWithParams.class.getMethod("someParam").getDefaultValue());
        assertEquals("tests.java.lang.annotation.AnnotationWithParams", AnnotationWithParams.class.getName());
    }
}
