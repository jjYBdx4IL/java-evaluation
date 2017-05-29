package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.net.SocketTimeoutException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class InstanceOfTest {

    interface Domestic {}
    class Animal {}
    class Dog extends Animal implements Domestic {}
    class Cat extends Animal implements Domestic {}
    class NiceDog extends Dog {}

    @Test
    public void testInstanceOfOp() {
        Object dog = new Dog();
        Object niceDog = new NiceDog();
        assertTrue(dog instanceof Animal);
        assertTrue(dog instanceof Domestic);
        assertTrue(niceDog instanceof Dog);
        assertFalse(dog instanceof NiceDog);
        assertFalse(dog instanceof Cat);
        // we get compilation errors for: Dog instanceof Cat etc.
    }

    @Test
    public void testDynamicInstanceOf() {
        Throwable ste = new SocketTimeoutException();
        Class<?> stec = SocketTimeoutException.class;
        Throwable ex = new Exception();
        Class<?> exc = Exception.class;

        assertFalse(ste.getClass().isInstance(SocketTimeoutException.class));
        assertFalse(ste.getClass().isInstance(stec));

        assertFalse(ex.getClass().isInstance(Exception.class));
        assertFalse(ex.getClass().isInstance(exc));

        assertFalse(ste.getClass().isInstance(Exception.class));
        assertFalse(ste.getClass().isInstance(exc));

        assertFalse(ex.getClass().isInstance(SocketTimeoutException.class));
        assertFalse(ex.getClass().isInstance(stec));
    }

    @Test
    public void testDynamicAssignableFrom() {
        Throwable ste = new SocketTimeoutException();
        Class<?> stec = SocketTimeoutException.class;
        Throwable ex = new Exception();
        Class<?> exc = Exception.class;

        assertTrue(ste.getClass().isAssignableFrom(stec));
        assertTrue(ex.getClass().isAssignableFrom(exc));

        assertFalse(ste.getClass().isAssignableFrom(exc));
        assertTrue(ex.getClass().isAssignableFrom(stec));
    }
}
