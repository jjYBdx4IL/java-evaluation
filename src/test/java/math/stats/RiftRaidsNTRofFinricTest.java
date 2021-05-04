package math.stats;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.apache.commons.math3.util.CombinatoricsUtils.factorial;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class RiftRaidsNTRofFinricTest {

    private static final Logger LOG = LoggerFactory.getLogger(RiftRaidsNTRofFinricTest.class);

    @Test
    public void brutalSwellProbabilityCalculation() {

        final int nPlayers = 10;
        final int nBrutalSwells = 13;
        final double dodgeChance = .95; // chance for a single player to dodge a single wave
        final int maxFailsPerWave = 1;

        double noWipeWaveProbability = 0.;
        for (int nFails=0; nFails<=maxFailsPerWave; nFails++) {
            final int nDodged = nPlayers - nFails;
            noWipeWaveProbability += Math.pow(dodgeChance, nDodged) * Math.pow(1.-dodgeChance, nFails)
                    * factorial(nPlayers) / factorial(nDodged) / factorial(nFails);
        }
        // optimistic probability that we don't wipe due to brutal swell during some run, ignoring all
        // other wipe sources
        double noWipeProbability = Math.pow(noWipeWaveProbability, nBrutalSwells);

        LOG.info("noWipeProbability = " + noWipeProbability);
    }

}
