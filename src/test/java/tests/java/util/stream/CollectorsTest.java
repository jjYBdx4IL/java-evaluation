package tests.java.util.stream;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectorsTest {

    @Test
    public void testListToMap() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        Map<String, String> result1 = list.stream().collect(
                Collectors.toMap(String::toLowerCase, String::toUpperCase));

        assertEquals("A", result1.get("a"));

        Map<String, String> result2 = list.stream().collect(
                Collectors.toMap(x -> x.toLowerCase(), x -> x.toUpperCase()));

        assertEquals("A", result2.get("a"));
    }
}
