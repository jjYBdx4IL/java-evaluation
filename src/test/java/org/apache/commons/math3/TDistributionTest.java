package org.apache.commons.math3;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.distribution.TDistribution;
import org.junit.Test;

public class TDistributionTest {

    TDistribution dist;
    
    @Test
    public void test() {
        dist = new TDistribution(4);
        assertEquals(2.0, dist.getNumericalVariance(), 1e-6);
        assertEquals(0.5, dist.cumulativeProbability(0), 1e-8);
        
        dist = new TDistribution(10);
        assertEquals(1.25, dist.getNumericalVariance(), 1e-6);
        assertEquals(0.5, dist.cumulativeProbability(0), 1e-8);
        assertEquals(0.880120, dist.cumulativeProbability(dist.getNumericalVariance()), 1e-6);
        
        dist = new TDistribution(100);
        assertEquals(1.020408d, dist.getNumericalVariance(), 1e-6);
        assertEquals(0.5, dist.cumulativeProbability(0), 1e-8);
        assertEquals(0.845001, dist.cumulativeProbability(dist.getNumericalVariance()), 1e-6);
        assertEquals(1.0, dist.cumulativeProbability(10), 1e-8);
    }
}
