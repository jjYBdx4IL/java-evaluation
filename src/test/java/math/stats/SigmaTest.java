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
     * <li>for trueModelProbability around 0.5, sdev is very well approximated
     * by sqrt(samplesize)/2.
     * <li>for small trueModelProbability, sdev is lower than
     * sqrt(trueModelProbability*samplesize), the approximation quality
     * increases with lower trueModelProbability values.
     * <li>for trueModelProbability > 0.5, sdev is the same as for
     * (1-trueModelProbability).
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

        double sampleMean = sampleSum / nSamples;
        double sumSquareDelta = 0.;
        for (int j = 0; j < nSamples; j++) {
            sumSquareDelta += Math.pow(sample[j] - sampleMean, 2.);
        }

        // t-student approximation of the stample standard deviation
        double sdev = Math.sqrt(sumSquareDelta / (nSamples - 1));
        LOG.info("sampleMean = " + sampleMean);
        LOG.info("sqrt(sampleMean) = " + Math.sqrt(sampleMean));
        LOG.info("sdev(t-student) = " + sdev);

        double nHits1Sigma = 0;
        double nHits2Sigma = 0;
        double nHits3Sigma = 0;
        for (int j = 0; j < nSamples; j++) {
            if (Math.abs(sample[j] - sampleMean) < 1 * sdev) {
                nHits1Sigma++;
            }
            if (Math.abs(sample[j] - sampleMean) < 2 * sdev) {
                nHits2Sigma++;
            }
            if (Math.abs(sample[j] - sampleMean) < 3 * sdev) {
                nHits3Sigma++;
            }
        }

        LOG.info("one sigma probability   = " + (nHits1Sigma / nSamples) + " (expected: 0.683)");
        LOG.info("two sigma probability   = " + (nHits2Sigma / nSamples) + " (expected: 0.954)");
        LOG.info("three sigma probability = " + (nHits3Sigma / nSamples) + " (expected: 0.997)");

        assertEquals(0.683, nHits1Sigma / nSamples, 0.03);
        assertEquals(0.954, nHits2Sigma / nSamples, 0.03);
        assertEquals(0.997, nHits3Sigma / nSamples, 0.03);
        
        // now use true sample mean instead of mean from samples
        sampleMean = sampleSize * trueModelProbability;
        nHits1Sigma = 0;
        nHits2Sigma = 0;
        nHits3Sigma = 0;
        for (int j = 0; j < nSamples; j++) {
            if (Math.abs(sample[j] - sampleMean) < 1 * sdev) {
                nHits1Sigma++;
            }
            if (Math.abs(sample[j] - sampleMean) < 2 * sdev) {
                nHits2Sigma++;
            }
            if (Math.abs(sample[j] - sampleMean) < 3 * sdev) {
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
