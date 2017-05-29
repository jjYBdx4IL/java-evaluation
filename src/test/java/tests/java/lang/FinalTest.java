package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class FinalTest {
    
    public class SomeContainer {
        public int i = 0;
    }
    
    @Test
    public void testModifiabilityOfElementsInFinalArray() {
        final char[] chars = new String("abc").toCharArray();
        chars[1] = 'z';
        assertEquals("azc", new String(chars));
    }
    
    @Test
    public void testFinalReturnValue() {
        SomeContainer sc = getSomeContainer();
        assertEquals(1, sc.i);
        sc.i = 2;
        sc = null;
    }
    
    @Ignore
    public final SomeContainer getSomeContainer() {
        final SomeContainer sc = new SomeContainer();
        sc.i = 1;
        return sc;
    }
}
