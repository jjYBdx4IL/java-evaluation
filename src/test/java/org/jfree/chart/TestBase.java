/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.jfree.chart;

import java.util.Random;

import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class TestBase extends InteractiveTestBase {

    public TimeSeries getTimeSeries() {
    	return getTimeSeries(0);
    }
    
    @SuppressWarnings("deprecation")
    public TimeSeries getTimeSeries(long seed) {
        final TimeSeries series = new TimeSeries( "Time Series" );
        RegularTimePeriod current = new Month( );
        double value = 100.0;
        final Random r = new Random(seed);
        for (int i = 0; i < 40; i++) {
			value = value + r.nextDouble() - 0.5;
			series.add(current, new Double( value ) );
			current = current.next( );
        }
        return series;
    }
    
    public JFreeChart getTimeSeriesChart() {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(getClass().getCanonicalName(),
        	      "Time",
        	      "Value",
        	      (XYDataset) new TimeSeriesCollection(getTimeSeries()),
        	      false,
        	      false,
        	      false);
        return chart;
    }
}
