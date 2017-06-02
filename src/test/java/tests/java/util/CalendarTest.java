package tests.java.util;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.Calendar;
import java.util.TimeZone;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CalendarTest {

    @Test
    public void testCalendarClone() {
        Calendar c = Calendar.getInstance();
        Calendar c2 = (Calendar) c.clone();
        assertEquals(c.getTimeInMillis(), c2.getTimeInMillis());
        assertEquals(c.getTimeZone().getDisplayName(), c2.getTimeZone().getDisplayName());
        assertEquals(c.get(Calendar.YEAR), c2.get(Calendar.YEAR));
        assertEquals(c.get(Calendar.MONTH), c2.get(Calendar.MONTH));
        assertEquals(c.get(Calendar.DAY_OF_MONTH), c2.get(Calendar.DAY_OF_MONTH));
        assertEquals(c.get(Calendar.HOUR), c2.get(Calendar.HOUR));
        assertEquals(c.get(Calendar.MINUTE), c2.get(Calendar.MINUTE));
        assertEquals(c.get(Calendar.SECOND), c2.get(Calendar.SECOND));
        assertEquals(c.get(Calendar.MILLISECOND), c2.get(Calendar.MILLISECOND));
    }

    @Test
    public void testCalendarClear() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        c.clear();
        assertEquals("CET", c.getTimeZone().getID());
        assertEquals(-3600 * 1000, c.getTimeInMillis());
        assertEquals(1970, c.get(Calendar.YEAR));
        assertEquals(0, c.get(Calendar.MONTH));
        assertEquals(1, c.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, c.get(Calendar.HOUR));
        assertEquals(0, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));
        assertEquals(0, c.get(Calendar.MILLISECOND));
    }

    @Test
    public void testMonthOffset() {
        assertEquals(0, Calendar.JANUARY);
    }

    @Test
    public void testOverflowHandling() {
        Calendar c = Calendar.getInstance();
        c.clear();
        int day = c.get(Calendar.DAY_OF_MONTH);
        c.add(Calendar.HOUR_OF_DAY, 26);
        assertEquals(day + 1, c.get(Calendar.DAY_OF_MONTH));
    }
}