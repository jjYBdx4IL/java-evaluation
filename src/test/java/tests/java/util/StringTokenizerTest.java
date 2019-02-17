package tests.java.util;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import java.util.StringTokenizer;

public class StringTokenizerTest {

    @Test
    public void test() {
        StringTokenizer st = new StringTokenizer("this; is a test");
        assertArrayEquals(new String[] {"this;", "is", "a", "test"}, toArray(st));
    }
    
    public static String[] toArray(StringTokenizer st) {
        String[] arr = new String[st.countTokens()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = st.nextToken();
        }
        return arr;
    }
}
