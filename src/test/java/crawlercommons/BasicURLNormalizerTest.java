package crawlercommons;

import static org.junit.Assert.assertEquals;

import crawlercommons.filters.basic.BasicURLNormalizer;
import org.junit.Test;

public class BasicURLNormalizerTest {

    @Test
    public void test() {
        BasicURLNormalizer normalizer = new BasicURLNormalizer();
        assertEquals("http://ü/", normalizer.filter("http://ü"));
    }
}
