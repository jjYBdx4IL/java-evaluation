/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package com.google.code.gson;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class GsonTest {

    private static final Logger LOG = LoggerFactory.getLogger(GsonTest.class);

    private Gson gson = new GsonBuilder().create();

    @Test
    public void testJsonEncoding() {
        assertEquals("\"\\\\\"", gson.toJson("\\")); // simple value quoting
        assertEquals("true", gson.toJson(true));
        JsonObject jo = new JsonObject();
        jo.addProperty("a", true);
        assertEquals("{\"a\":true}", gson.toJson(jo));
    }
    
    @Test
    public void test1() {
        Person person = gson.fromJson("{\"name\": \"John\"}", Person.class);
        LOG.info(person.toString());
        LOG.info(gson.toJson(person));
    }

    // this will only work with simple key and value types
    // - use GsonBuilder().enableComplexMapKeySerialization() for default
    // handling of complex types.
    @Test
    public void testMapSimple() {
        // serialization
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        String json = gson.toJson(map);
        assertEquals("{\"one\":1,\"two\":2}", json);

        // de-serialization, into *Double* by default
        @SuppressWarnings("unchecked")
        Map<String, Double> map2 = gson.fromJson(json, Map.class);
        assertEquals((Double)1d, map2.get("one"));
        assertEquals((Double)2d, map2.get("two"));
        
        // better, give it some hint:
        Map<String, Integer> map3 = gson.fromJson(json, new TypeToken<Map<String, Integer>>(){}.getType());
        assertEquals((Integer)1, map3.get("one"));
        assertEquals((Integer)2, map3.get("two"));
    }

    /**
     * http://jenkins/depgraph-view/graph.json
     */
    @Test
    public void testJenkinsJSONDepGraph() {
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse("{\n"
            + "  \"edges\":   [\n"
            + "        {\n"
            + "      \"from\": \"maven\",\n"
            + "      \"to\": \"android\",\n"
            + "      \"type\": \"dep\"\n"
            + "    },\n"
            + "        {\n"
            + "      \"from\": \"parent\",\n"
            + "      \"to\": \"android\",\n"
            + "      \"type\": \"dep\"\n"
            + "    }]}");
        JsonArray arr = root.getAsJsonObject().get("edges").getAsJsonArray();
        Iterator<JsonElement> it = arr.iterator();
        while (it.hasNext()) {
            JsonElement el = it.next();
            Edge edge = gson.fromJson(el.toString(), Edge.class);
            LOG.info(edge.toString());
        }
    }

}
