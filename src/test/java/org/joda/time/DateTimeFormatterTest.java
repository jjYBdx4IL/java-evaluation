/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.joda.time;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Github jjYBdx4IL Projects
 *
 */
public class DateTimeFormatterTest {
	
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
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();
		DateTime dt = formatter.parseDateTime("1929-01-01");
		// joda time stuff does NOT depend on Java's global TZ or locale setting!
		assertEquals("1929-01-01T00:00:00.000Z", dt.toString());
		assertEquals("Tue Jan 01 00:00:00 UTC 1929", dt.toDate().toString());
	}

	@Test
	public void testParseBeforeEpoch2() throws ParseException {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();
		DateTime dt = formatter.parseDateTime("1929-03-21");
		// joda time stuff does NOT depend on Java's global TZ or locale setting!
		assertEquals("1929-03-21T00:00:00.000Z", dt.toString());
		assertEquals("Thu Mar 21 00:00:00 UTC 1929", dt.toDate().toString());
	}

	@Test
	public void testISODateFormat() {
		final DateTimeFormatter formatter 
			= ISODateTimeFormat.basicDateTime().withZoneUTC().withLocale(Locale.ROOT);
		assertEquals("19700101T000000.000Z", formatter.print(0));
	}
	
}
