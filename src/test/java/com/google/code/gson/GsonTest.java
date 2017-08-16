/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package com.google.code.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Iterator;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class GsonTest {

    private static final Logger log = LoggerFactory.getLogger(GsonTest.class);

    @Test
    public void test1() {
        Gson g = new Gson();
        Person person = g.fromJson("{\"name\": \"John\"}", Person.class);
        log.info(person.toString());
        log.info(g.toJson(person));
    }

    /**
     * http://jenkins/depgraph-view/graph.json
     */
    @Test
    public void testJenkinsJSONDepGraph() {
        Gson g = new Gson();
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
            Edge edge = g.fromJson(el.toString(), Edge.class);
            log.info(edge.toString());
        }
    }

}
