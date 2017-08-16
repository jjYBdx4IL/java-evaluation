package math.stats;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.*;

import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * How many consecutive tries do we need on average to win the next game
 * if the win chance of one game is 50%?
 * 
 * @author Github jjYBdx4IL Projects
 */
public class AvgTriesNeededToWinBasedOnSingleTryWinChance {

	private static final Logger log = Logger.getLogger(AvgTriesNeededToWinBasedOnSingleTryWinChance.class.getName());
	private Random r;
	
	@Before
	public void before() {
		r = new Random(0);
	}
	
	@Test
	public void test() {
		final double singleWinChance = 0.5;
		final int nSamples = 1000 * 1000;
		long counter = 0;
		for (int i=0; i<nSamples; i++) {
			counter += sampleNTriesForNextWin(singleWinChance);
		}
		double avg = (double) counter / nSamples;
		log.info(avg);
		assertEquals(2.0, avg, 0.005);
	}
	
	private long sampleNTriesForNextWin(double winChance) {
		long nTries = 0;
		do {
			nTries++;
		} while (r.nextDouble() > winChance);
		return nTries;
	}

}
