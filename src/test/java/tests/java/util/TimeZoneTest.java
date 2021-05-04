package tests.java.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.AfterClass;
import org.junit.Test;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TimeZoneTest {
    
    private static final TimeZone defaultTimeZoneBackup = TimeZone.getDefault();
    
    @AfterClass
    public static void afterClass() {
        // restore default timezone to avoid introducing interdependencies between the test units...
        // (avoid using compile-on-save in Netbeans... it will save the class using lmod time from
        // the wrong timezone and therefore refuse to update it upon further changes!)
        TimeZone.setDefault(defaultTimeZoneBackup);
    }
    
    /**
     * SimpleDateFormat seems to use the default timezone that was active during its constructor
     * execution...
     * 
     * @throws ParseException 
     */
    @Test
    public void testSimpleDateFormatDefaultTimeZone() throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long millis = sf.parse("1927-12-31 23:54:07").getTime();
        
         
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        long millis2 = sf.parse("1927-12-31 23:54:07").getTime();
        assertEquals(millis, millis2);
        
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long millis3 = sf.parse("1927-12-31 23:54:07").getTime();
        assertNotEquals(millis, millis3);
    }


    /**
     * @url http://stackoverflow.com/questions/6841333/why-is-subtracting-these-two-times-in-1927-giving-a-strange-result
     * @url http://stackoverflow.com/questions/21784002/did-jdk7u25-introduce-a-timezone-bug
     * 
     * @throws java.text.ParseException
     */
    @Test
    public void testAsiaTZRegressionJDKBug7070044() throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long millis = sf.parse("1927-12-31 23:54:08").getTime()
            - sf.parse("1927-12-31 23:54:07").getTime();
        assertEquals(1000L, millis);
    }
}