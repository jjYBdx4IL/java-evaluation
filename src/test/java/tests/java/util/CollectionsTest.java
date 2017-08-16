/*
 * #%L
 * evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author Github jjYBdx4IL Projects
 *
 */
public class CollectionsTest {

	private static final Logger log = Logger.getLogger(CollectionsTest.class);
	private static final int LIST_LENGTH = 1000 * 1000;
	private static final long SLOW_ITERATION_MILLIS = 1000L;
	private static final int MAX_TRIES = 100;
	
	@Test
	public void testBinarySearch() {
		Random r = new Random(0);
		List<Double> list = new ArrayList<>(LIST_LENGTH);
		for (int i = 0; i < LIST_LENGTH; i++) {
			list.add(r.nextDouble());
		}
		Collections.sort(list);
		list = new ArrayList<>(list);

		int nTries = 0;
		double speedUp = 0.0;
		double minExpectedSpeedUp = 100.0;
		double slowFindsPerSec;
		double fastFindsPerSec;
		
		do {
			r = new Random(0);
			int nIterations = 0;
			long startTime = System.currentTimeMillis();
			do {
				int index = r.nextInt(LIST_LENGTH);
				assertEquals(index, list.indexOf(list.get(index)));
				nIterations++;
			} while (System.currentTimeMillis() < startTime + SLOW_ITERATION_MILLIS);
			long duration = System.currentTimeMillis() - startTime;
			slowFindsPerSec = (double) nIterations / duration * 1000;
			
			r = new Random(0);
			nIterations = 0;
			startTime = System.currentTimeMillis();
			do {
				int index = r.nextInt(LIST_LENGTH);
				assertEquals(index, Collections.binarySearch(list, list.get(index)));
				nIterations++;
			} while (System.currentTimeMillis() < startTime + SLOW_ITERATION_MILLIS);
			duration = System.currentTimeMillis() - startTime;
			fastFindsPerSec = (double) nIterations / duration * 1000;
			
			speedUp = fastFindsPerSec / slowFindsPerSec;
			nTries++;
		} while (speedUp < minExpectedSpeedUp && nTries < MAX_TRIES);
		
		log.info(String.format(
				Locale.ROOT,
				"binary search on ArrayList of size %d is %f times faster than ArrayList.indexOf()",
				list.size(),
				speedUp
				));
		log.info(String.format(Locale.ROOT, "%f vs %f finds per second", fastFindsPerSec, slowFindsPerSec));
		
		assertTrue(speedUp >= minExpectedSpeedUp);
	}
		
}
