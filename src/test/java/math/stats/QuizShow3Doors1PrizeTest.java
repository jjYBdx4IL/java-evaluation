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

public class QuizShow3Doors1PrizeTest {

    private static final Logger LOG = LoggerFactory.getLogger(QuizShow3Doors1PrizeTest.class);
    private Random r;

    @Before
    public void before() {
        r = new Random(0L);
    }

    /**
     * There are 3 doors and 1 prize. After your first selection, the moderator
     * opens a loser door. Would you change your selection after that?
     * <p>
     * Strategy 1: remain with first decision. win chance is 1/3.<br>
     * Strategy 2: change your selection =&gt; win chance should be 2/3.
     */
    @Test
    public void testQuizShow3Doors1PrizeStrategy1() {
        double expectedWinRatio = 1. / 3.;
        double nSigma = 5.;
        // five sigma shall equal 1% of all iterations (1% win chance)
        double oneSigmaRelSize = .01 / nSigma;
        long nIter = (long) (1. / (oneSigmaRelSize * oneSigmaRelSize));
        LOG.info("iterations: " + nIter);

        int nWins = 0;
        for (int i = 0; i < nIter; i++) {
            int mySelection = r.nextInt(3);
            int prizePos = r.nextInt(3);
            if (mySelection == prizePos) {
                nWins++;
            }
        }
        double winRatio = (double) nWins / nIter;
        LOG.info("win ratio: " + winRatio);
        assertEquals(expectedWinRatio, winRatio, oneSigmaRelSize * nSigma);
    }

    /**
     * @see {@link #testQuizShow3Doors1PrizeStrategy1()}
     */
    @Test
    public void testQuizShow3Doors1PrizeStrategy2() {
        double expectedWinRatio = 2. / 3.;
        double nSigma = 5.;
        // five sigma shall equal 1% of all iterations (1% win chance)
        double oneSigmaRelSize = .01 / nSigma;
        long nIter = (long) (1. / (oneSigmaRelSize * oneSigmaRelSize));
        LOG.info("iterations: " + nIter);

        int nWins = 0;
        for (int i = 0; i < nIter; i++) {
            int mySelection = r.nextInt(3);
            int prizePos = r.nextInt(3);

            // moderator chooses a loser door
            int openDoorSelection = -1;
            while (openDoorSelection < 0) {
                int randomSelection = r.nextInt(3);
                if (randomSelection != mySelection && randomSelection != prizePos) {
                    openDoorSelection = randomSelection;
                }
            }

            // I switch my selection to the remaining door:
            mySelection = (mySelection + 1) % 3;
            if (mySelection == openDoorSelection) {
                mySelection = (mySelection + 1) % 3;
            }

            if (mySelection == prizePos) {
                nWins++;
            }
        }
        double winRatio = (double) nWins / nIter;
        LOG.info("win ratio: " + winRatio);
        assertEquals(expectedWinRatio, winRatio, oneSigmaRelSize * nSigma);
    }

}
