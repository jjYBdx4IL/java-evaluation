package org.apache.commons.math3;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.stat.Frequency;
import org.junit.Test;

public class FrequencyTest {

    @Test
    public void testInt() {
        Frequency freq1 = new Frequency();
        Frequency freq2 = new Frequency();
        
        freq1.addValue(1);
        freq1.addValue(1);
        freq1.addValue(3);
        freq1.addValue(2);
        
        freq2.addValue(4);
        freq2.addValue(2);

        System.out.println(freq1);
        System.out.println(freq2);
        freq1.merge(freq2);
        System.out.println(freq1);
        System.out.println(freq2);
        
        assertEquals(2, freq1.getCount(2));
    }
    
    @Test
    public void testString() {
        Frequency freq1 = new Frequency();
        
        freq1.addValue("one");
        freq1.addValue("one");
        freq1.addValue("two");

        System.out.println(freq1);
        
        assertEquals(2, freq1.getCount("one"));
        assertEquals(2, freq1.getUniqueCount());
        assertEquals(3, freq1.getSumFreq());
    }
}
