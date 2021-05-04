package tests.java.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationWithParams {
    public final static int DEFAULT = 4;
    int retries() default 3;
    int someParam() default DEFAULT;
}
