package tests.java.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Github jjYBdx4IL Projects
 *
 */
@SuppressWarnings("all")
public class DateTest {
    
    private static TimeZone defaultTimeZoneBeforeTest;
    private static Locale defaultLocaleBeforeTest;

    @BeforeClass
    public static void setUpClass() throws Exception {
        defaultTimeZoneBeforeTest = TimeZone.getDefault();
        defaultLocaleBeforeTest = Locale.getDefault();
    }

    @After
    public void tearDownClass() throws Exception {
        TimeZone.setDefault(defaultTimeZoneBeforeTest);
        Locale.setDefault(defaultLocaleBeforeTest);
    }
    
    @Before
    public void prepare() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1:00"));
    }

    @Test
    public void testCalendarClearTimeZone() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"), Locale.ROOT);
        cal.clear();
        // clear() does not clear the timezone property:
        assertEquals(TimeZone.getTimeZone("Europe/Berlin"), cal.getTimeZone());
        
        cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ROOT);
        cal.clear();
        assertEquals(TimeZone.getTimeZone("UTC"), cal.getTimeZone());
    }
    
    @Test
    public void testCalendarTimeZoneAndClear() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"), Locale.ROOT);
        assertTrue(cal instanceof GregorianCalendar);
        
        cal.clear();
        cal.set(1970, 0, 1);
        // "epoch" in GMT+1 gives -1 hour in UTC
        assertEquals(-3600L*1000L, cal.getTimeInMillis());
        assertEquals(TimeZone.getTimeZone("Europe/Berlin"), cal.getTimeZone());
        assertEquals(1970, cal.get(Calendar.YEAR));
        assertEquals(0, cal.get(Calendar.MONTH));
        // the set/get date-part related methods are always relative to the calendar's timezone:
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(1969, cal.get(Calendar.YEAR));
        assertEquals(11, cal.get(Calendar.MONTH));
        
        // setTimeInMillis does *NOT* change the timezone setting:
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        cal.setTimeInMillis(System.currentTimeMillis());
        assertEquals(TimeZone.getTimeZone("Europe/Berlin"), cal.getTimeZone());
        
        
        Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ROOT);
        cal2.clear();
        cal2.set(1970, 0, 1);
        // epoch in UTC gives 0
        assertEquals(0L, cal2.getTimeInMillis());
        // getTime returns Date in UTC:
        assertEquals(0L, cal2.getTime().getTime());
        assertEquals(TimeZone.getTimeZone("UTC"), cal2.getTimeZone());
        assertEquals(1970, cal2.get(Calendar.YEAR));
        
        cal2.setTimeInMillis(System.currentTimeMillis());
        assertEquals(TimeZone.getTimeZone("UTC"), cal2.getTimeZone());
    }
    
    @Test
    public void testCalendar() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ROOT);
        c.setTimeInMillis(0);
        assertEquals(0, c.getTimeInMillis());
        assertEquals(0, c.getTimeZone().getOffset(c.getTimeInMillis()));
        assertEquals(60*60*1000, TimeZone.getTimeZone("Europe/Berlin").getOffset(c.getTimeInMillis()));
    }
    
    @Test
    public void testClone() {
        Date one = new Date(123);
        Date two = (Date) one.clone();
        assertEquals(123, two.getTime());
        one.setTime(456);
        assertEquals(456, one.getTime());
        assertEquals(123, two.getTime());
        
        two = (Date) null;
    }
    
    @Test
    public void testDateNewGivesCurrentTime() {
        Date d = new Date();
        long millis = System.currentTimeMillis();
        assertTrue(d.getTime() <= millis && d.getTime() + 600L*1000L > millis);
    }
    
    /**
     * This test demonstrates that Date does not contain timezone information.
     */
    @Test
    public void testDateTZContents() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Date d = new Date(0); // 0 seconds since the epoch in UTC
        assertEquals("Thu Jan 01 00:00:00 UTC 1970", d.toString());
        
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1:00"));
        assertEquals("Thu Jan 01 01:00:00 GMT+01:00 1970", d.toString());
    }
    
    /**
     * This test demonstrates that the default timezone does *not* matter when creating
     * a Date object from UTC millis.
     */
    @Test
    public void testDateTZCreation() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1:00"));
        Date d1 = new Date(0);
        
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Date d2 = new Date(0);
        
        assertEquals("Thu Jan 01 00:00:00 UTC 1970", d1.toString());
        assertEquals("Thu Jan 01 00:00:00 UTC 1970", d2.toString());
        assertEquals(d1.toString(), d2.toString());
    }
    
    @Test
    public void testDate() {
        Date d = new Date(0); // 0 seconds since the epoch in UTC
        assertEquals(0, d.getTime());
        assertEquals(-60, d.getTimezoneOffset());
        assertEquals("Thu Jan 01 01:00:00 GMT+01:00 1970", d.toString());
        assertEquals(-60, (d.getTime() - Date.UTC(d.getYear(),
                       d.getMonth(),
                       d.getDate(),
                       d.getHours(),
                       d.getMinutes(),
                       d.getSeconds())) / (60 * 1000));
        
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        assertEquals(0, d.getTime());
        assertEquals(0, d.getTimezoneOffset());
        assertEquals("Thu Jan 01 00:00:00 UTC 1970", d.toString());
        assertEquals(0, (d.getTime() - Date.UTC(d.getYear(),
                       d.getMonth(),
                       d.getDate(),
                       d.getHours(),
                       d.getMinutes(),
                       d.getSeconds())) / (60 * 1000));
        
        d = new Date(0); // 0 seconds since the epoch in UTC
        
        assertEquals(0, d.getTime());
        assertEquals(0, d.getTimezoneOffset());
        assertEquals("Thu Jan 01 00:00:00 UTC 1970", d.toString());
        assertEquals(0, (d.getTime() - Date.UTC(d.getYear(),
                       d.getMonth(),
                       d.getDate(),
                       d.getHours(),
                       d.getMinutes(),
                       d.getSeconds())) / (60 * 1000));
    }

    @Test
    public void testDeprDateConstructorReplacementUTC() throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        int day = 1;
        int month = 1;
        int year = 1900;

        @SuppressWarnings("deprecation")
        Date d1 = new Date(year - 1900, month - 1, day);         Calendar c1 = Calendar.getInstance();
        c1.clear();
        c1.set(year, month - 1, day);
        assertEquals(d1.toString(), c1.getTime().toString());
        assertEquals(d1.getTime(), c1.getTimeInMillis());

        Calendar c2 = GregorianCalendar.getInstance();
        c2.clear();
        c2.set(year, month - 1, day);
        assertEquals(d1.toString(), c2.getTime().toString());
        assertEquals(d1.getTime(), c2.getTimeInMillis());
    }

    @Test
    public void testDeprDateConstructorReplacementNonUTC() throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));

        int day = 1;
        int month = 1;
        int year = 1900;

        @SuppressWarnings("deprecation")
        Date d1 = new Date(year - 1900, month - 1, day);         Calendar c1 = Calendar.getInstance();
        c1.clear();
        c1.set(year, month - 1, day);
        assertEquals(d1.toString(), c1.getTime().toString());
        assertEquals(d1.getTime(), c1.getTimeInMillis());

        Calendar c2 = GregorianCalendar.getInstance();
        c2.clear();
        c2.set(year, month - 1, day);
        assertEquals(d1.toString(), c2.getTime().toString());
        assertEquals(d1.getTime(), c2.getTimeInMillis());
    }
}
