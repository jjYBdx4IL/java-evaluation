package tests.java.lang.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author jjYBdx4IL
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationsWithValueList {
    String[] values() default {};
}
