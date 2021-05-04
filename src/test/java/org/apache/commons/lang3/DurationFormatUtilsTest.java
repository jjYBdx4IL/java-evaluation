package org.apache.commons.lang3;

import org.apache.commons.lang3.time.DurationFormatUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class DurationFormatUtilsTest {

    @Test
    public void test() {
        assertEquals("0:01:01", DurationFormatUtils.formatDuration(61001L, "H:mm:ss"));
        assertEquals("1:01", DurationFormatUtils.formatDuration(61001L, "m:ss"));
        assertEquals("01:01", DurationFormatUtils.formatDuration(61001L, "mm:ss"));
    }
}
