package tests.java.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathTest {

    public static final double PREC = 1e-6;
    
    @Test
    public void testIEEEremainder() {
        assertEquals(0, Math.IEEEremainder(1, 1), PREC);
        assertEquals(1.0, Math.IEEEremainder(1, 2), PREC);
        assertEquals(0, Math.IEEEremainder(0, 1), PREC);
        assertEquals(0.2, Math.IEEEremainder(0.2, 1), PREC);
        assertEquals(0, Math.IEEEremainder(1, 1), PREC);
        assertEquals(-0.2, Math.IEEEremainder(1.8, 1), PREC);
        assertEquals(0, Math.IEEEremainder(2, 1), PREC);
    }
}
