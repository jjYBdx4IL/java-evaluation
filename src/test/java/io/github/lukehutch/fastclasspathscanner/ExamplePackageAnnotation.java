package io.github.lukehutch.fastclasspathscanner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author work
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PACKAGE)
public @interface ExamplePackageAnnotation {
    
}
