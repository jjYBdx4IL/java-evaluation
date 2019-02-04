package tests.java.lang;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContinueTest {

    @Test
    public void testContinue() {
        int nFor = 0;
        int i = 0;
        while(i < 1) {
            i++;
            for (int j=0; j<10; j++) {
                nFor++;
                continue; // jumps to "for"
            }
        }
        assertEquals(10, nFor);
    }
    
    @Test
    public void testContinueWithLabel() {
        int nFor = 0;
        int i = 0;
        OUTER: while(i < 1) {
            i++;
            for (int j=0; j<10; j++) {
                nFor++;
                continue OUTER;
            }
        }
        assertEquals(1, nFor);
    }
}
