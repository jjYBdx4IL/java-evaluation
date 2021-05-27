package tests.java.time;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

public class InstantTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(InstantTest.class);
    
    @Test
    public void testPlus() {
        Instant i = Instant.EPOCH;
        assertEquals("1970-01-01T00:00:00Z", i.toString());
        
        i.plusSeconds(3600);
        assertEquals("1970-01-01T00:00:00Z", i.toString());
        
        Instant j = i.plusSeconds(3600);
        assertEquals("1970-01-01T01:00:00Z", j.toString());
        
        LOG.info(Instant.now().toString());
    }
}
