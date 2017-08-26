package math.stats;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.*;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SigmaTest {

    private static final Logger LOG = LoggerFactory.getLogger(SigmaTest.class);
    private Random r;

    @Before
    public void before() {
        r = new Random(0);
    }

    /**
     * expected values: 1 sigma -> 68.3%, 2 sigma -> 95.4%, 3 sigma -> 99.7%
     * <p>
     * Observations:
     * <ul>
     * <li>for trueModelProbability around 0.5, sdev is very well approximated by sqrt(samplesize)/2.
     * <li>for small trueModelProbability, sdev is lower than sqrt(trueModelProbability*samplesize),
     * the approximation quality increases with lower trueModelProbability values.
     * <li>for trueModelProbability > 0.5, sdev is the same as for (1-trueModelProbability).
     * </ul>
     */
    @Test
    public void testSigmaSampleProbability() {
        double nSamples = 10000;
        double sampleSize = 1000;
        double trueModelProbability = 0.3;

        double[] sample = new double[(int) nSamples];
        double sampleSum = 0.;
        for (int j = 0; j < nSamples; j++) {
            double sampleValue = 0;
            for (int n = 0; n < sampleSize; n++) {
                if (r.nextDouble() < trueModelProbability) {
                    sampleValue++;
                }
            }
            sampleSum += sample[j] = sampleValue;
        }

        double sampleAverage = sampleSum / nSamples;
        double sumSquareDelta = 0.;
        for (int j = 0; j < nSamples; j++) {
            sumSquareDelta += Math.pow(sample[j] - sampleAverage, 2.);
        }

        // t-student approximation of the stample standard deviation
        double sdev = Math.sqrt(sumSquareDelta / nSamples);
        LOG.info("sampleAverage = " + sampleAverage);
        LOG.info("sdev(t-student) = " + sdev);

        double nHits1Sigma = 0;
        double nHits2Sigma = 0;
        double nHits3Sigma = 0;
        for (int j = 0; j < nSamples; j++) {
            if (sampleAverage - 1. * sdev < sample[j] && sample[j] < sampleAverage + sdev * 1.) {
                nHits1Sigma++;
            }
            if (sampleAverage - 2. * sdev < sample[j] && sample[j] < sampleAverage + sdev * 2.) {
                nHits2Sigma++;
            }
            if (sampleAverage - 3. * sdev < sample[j] && sample[j] < sampleAverage + sdev * 3.) {
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
