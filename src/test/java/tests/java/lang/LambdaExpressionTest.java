package tests.java.lang;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LambdaExpressionTest {

    @Test
    public void testLambdaExpression() {
        assertArrayEquals(new String[] {}, filter(new String[] {},
            s -> false));
        assertArrayEquals(new String[] {}, filter(new String[] {},
            null));
        assertArrayEquals(new String[] {}, filter(new String[] { "" },
            s -> s != null && !s.isEmpty()));
        assertArrayEquals(new String[] {}, filter(new String[] { null },
            s -> s != null && !s.isEmpty()));
        assertArrayEquals(new String[] { "1", "2" }, filter(new String[] { "1", null, "2", "" },
            s -> s != null && !s.isEmpty()));
        assertArrayEquals(new String[] {}, filter(new String[] { "1", null, "2", "" },
            s -> false));
    }

    @Test(expected = NullPointerException.class)
    public void testNpe() {
        filter(new String[] { "" }, null);
    }

    private String[] filter(String[] strings, FuncIface fi) {
        List<String> result = new ArrayList<>();
        for (String i : strings) {
            if (fi.test(i)) {
                result.add(i);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    // "functional interface", defined by: "contains only one abstract method"
    // (though it may more methods
    // of other types
    interface FuncIface {
        boolean test(String input);
    }
}
