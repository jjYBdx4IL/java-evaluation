package tests.java.lang;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.inject.Inject;

//@meta:keywords:reflection,reflect,declared,field,access@
public class ReflectTest {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ReflectTest.class);
    
    @Inject
    int one = 123;

    //@meta:keywords:declared,field,access,annotation,injection@
    @Test
    public void testFieldAccess() throws Exception {
        assertEquals(0, getClass().getFields().length);
        assertEquals(2, getClass().getDeclaredFields().length);
        
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
    
    @Test
    public void testInstanceCreation() throws Exception {
        Class<?> klazz = Class.forName(TestMe.class.getName());
        
        // https://docs.oracle.com/javase/tutorial/reflect/member/ctorInstance.html
        Constructor<?> ctor = klazz.getDeclaredConstructors()[0];
        assertEquals("class java.lang.String", ctor.getGenericParameterTypes()[0].toString());
        assertEquals("int", ctor.getGenericParameterTypes()[1].toString());

        assertTrue(ctor.getGenericParameterTypes()[0].equals(String.class));
        
        TestMe tm = (TestMe) ctor.newInstance("a", 3);
        assertEquals("a", tm.s);
        assertEquals(3, tm.i);
    }
    
    @Test
    public void testStaticMethodAccess() throws Exception {
        Class<?> klazz = Class.forName(TestMe.class.getName());
        
        Method m = klazz.getDeclaredMethod("getThree");
        assertEquals(int.class, m.getReturnType());
        
        int a = (int) m.invoke(null);
        assertEquals(3, a);
    }
    
    public static class TestMe {
        public String s;
        public int i;
        public TestMe(String s, int i) {
            this.s = s;
            this.i = i;
        }
        static int getThree() {
            return 3;
        }
    }
}
