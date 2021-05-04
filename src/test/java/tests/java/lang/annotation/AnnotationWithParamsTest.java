package tests.java.lang.annotation;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class AnnotationWithParamsTest {

    @Test
    public void test() throws NoSuchMethodException, ClassNotFoundException {
        assertEquals(3, (int) AnnotationWithParams.class.getMethod("retries").getDefaultValue());
        assertEquals(4, (int) AnnotationWithParams.class.getMethod("someParam").getDefaultValue());
        assertEquals("tests.java.lang.annotation.AnnotationWithParams", AnnotationWithParams.class.getName());
        
        assertEquals(-123, AnnotatedClass.class.getAnnotation(AnnotationWithParams.class).someParam());
        assertArrayEquals(new String[]{"one", "two"},
                AnnotatedClass.class.getAnnotation(AnnotationsWithValueList.class).values());
        
        Class<?> packageClass = Class.forName(getClass().getPackage().getName()+".package-info", false, Thread.currentThread().getContextClassLoader());
        assertArrayEquals(new String[]{"one", "two"},
                packageClass.getAnnotation(AnnotationsWithValueList.class).values());
        
        assertArrayEquals(new String[]{"one", "two"},
                getClass().getPackage().getAnnotation(AnnotationsWithValueList.class).values());
        
    }
}
