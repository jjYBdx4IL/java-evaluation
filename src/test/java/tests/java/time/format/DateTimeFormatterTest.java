package tests.java.time.format;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterTest {

    @Test
    public void test() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(0L, 0, ZoneOffset.UTC);
        assertEquals("1970 01 01", ldt.format(formatter));
        ldt = LocalDateTime.ofEpochSecond(86400L * 3L, 0, ZoneOffset.UTC);
        assertEquals("1970 01 04", ldt.format(formatter));
    }
}
