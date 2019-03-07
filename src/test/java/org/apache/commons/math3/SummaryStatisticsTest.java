package org.apache.commons.math3;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class SummaryStatisticsTest {

    private static final Logger LOG = LoggerFactory.getLogger(SummaryStatisticsTest.class);
    private Random r;

    @Before
    public void before() {
        r = new Random(0);
    }

    @Test
    public void test() {
        SummaryStatistics sumStats = new SummaryStatistics();

        double nSamples = 10000;
        double sampleSize = 1000;
        double trueModelProbability = 0.3;
        double[] sample = new double[(int) nSamples];

        for (int j = 0; j < nSamples; j++) {
            double sampleValue = 0;
            for (int n = 0; n < sampleSize; n++) {
                if (r.nextDouble() < trueModelProbability) {
                    sampleValue++;
                }
            }
            sample[j] = sampleValue;
            sumStats.addValue(sampleValue);
        }

        System.out.println(sumStats);
        double sdev = sumStats.getStandardDeviation();
        double mean = sumStats.getMean();

        double nHits1Sigma = 0;
        double nHits2Sigma = 0;
        double nHits3Sigma = 0;
        for (int j = 0; j < nSamples; j++) {
            if (Math.abs(sample[j] - mean) < 1 * sdev) {
                nHits1Sigma++;
            }
            if (Math.abs(sample[j] - mean) < 2 * sdev) {
                nHits2Sigma++;
            }
            if (Math.abs(sample[j] - mean) < 3 * sdev) {
                nHits3Sigma++;
            }
        }

        LOG.info("one sigma probability   = " + (nHits1Sigma / nSamples) + " (expected: 0.683)");
        LOG.info("two sigma probability   = " + (nHits2Sigma / nSamples) + " (expected: 0.954)");
        LOG.info("three sigma probability = " + (nHits3Sigma / nSamples) + " (expected: 0.997)");

        assertEquals(0.683, nHits1Sigma / nSamples, 0.03);
        assertEquals(0.954, nHits2Sigma / nSamples, 0.03);
        assertEquals(0.997, nHits3Sigma / nSamples, 0.03);
    }
}
