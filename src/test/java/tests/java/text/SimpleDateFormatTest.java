/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Github jjYBdx4IL Projects
 *
 */
public class SimpleDateFormatTest {

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
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Locale.setDefault(Locale.ROOT);
    }
    
	@Test
	public void testParseBeforeEpoch() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		assertEquals("1929-01-01", sdf.format(sdf.parse("1929-01-01")));
	}
	
    @Test
    public void testSimpleDateFormat() throws ParseException {
    	TimeZone.setDefault(TimeZone.getTimeZone("GMT+1:00"));
    	
        // parser assumes default timezone for input string if it does not contain any timezone information:
        DateFormat stdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals("Sat Feb 03 14:15:16 GMT+01:00 2001", stdf.parse("2001-02-03 14:15:16").toString());
        
        stdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        assertEquals("Sat Feb 03 14:15:16 GMT+01:00 2001", stdf.parse("2001-02-03 13:15:16 UTC").toString());

        stdf = new SimpleDateFormat("yyyy-MM-dd Z");
        assertEquals("Sat Feb 03 01:00:00 GMT+01:00 2001", stdf.parse("2001-02-03 UTC").toString());

        stdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        Date d = stdf.parse("2001-02-03 13:15:16 UTC");
        stdf = new SimpleDateFormat("yyyy MM dd HH mm ss");
        stdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals("2001 02 03 13 15 16", stdf.format(d));
        
        stdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
        assertEquals("2001-02-03 14:15:16.000 +0100", stdf.format(d));
    }
    
    @Test
    public void testParseEffectOnTZ() throws ParseException {
    	TimeZone.setDefault(TimeZone.getTimeZone("GMT+1:00"));
    	
    	DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    	assertEquals(TimeZone.getTimeZone("UTC"), sdf.getTimeZone());
    	sdf.parse("2012-05-05");
    	assertEquals(TimeZone.getTimeZone("UTC"), sdf.getTimeZone());
    }
    
}
