package tests.java.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.lang.reflect.Field;

import javax.inject.Inject;

//@meta:keywords:reflection,reflect,declared,field,access@
public class ReflectTest {

    @Inject
    int one = 123;

    //@meta:keywords:declared,field,access,annotation,injection@
    @Test
    public void testFieldAccess() throws Exception {
        assertEquals(0, getClass().getFields().length);
        assertEquals(1, getClass().getDeclaredFields().length);
        
        // retrieve field value via reflection
        Field f = ReflectTest.class.getDeclaredField("one");
        assertEquals(int.class, f.getType());
        assertEquals("one", f.getName());
        assertEquals("ReflectTest", f.getDeclaringClass().getSimpleName());
        assertEquals(123, f.getInt(this));
        
        // update via reflection
        f.set(this, 124);
        assertEquals(124, one);
        
        // can be found via its annotation, ie. for injection
        assertTrue(f.isAnnotationPresent(Inject.class));
    }

    @Test(expected = NoSuchFieldException.class)
    public void testNotExisting() throws Exception {
        getClass().getField("notexisting");
    }
}
