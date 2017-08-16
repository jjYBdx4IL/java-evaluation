package com.google.code.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CustomDeserializerTest {

    private static final Logger LOG = LoggerFactory.getLogger(CustomDeserializerTest.class);

    public class StringArrayDeserializer implements JsonDeserializer<String[]> {

        @Override
        public String[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            LOG.info(json.toString());
            if (json.isJsonNull()) {
                return new String[]{};
            }
            if (json.isJsonPrimitive()) {
                return new String[]{json.getAsString()};
            }
            if (json.isJsonObject()) {
                throw new JsonParseException("exptected String[], found JsonObject");
            }
            JsonArray arr = json.getAsJsonArray();
            String[] result = new String[arr.size()];
            int i = 0;
            for (JsonElement el : arr) {
                result[i] = el.getAsString();
                i++;
            }
            return result;
        }

    }

    @Test
    public void test() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(String[].class, new StringArrayDeserializer());
        Gson gson = gsonBuilder.create();

        User user = gson.fromJson("{names:[\"a\", \"b\"]}", User.class);
        assertArrayEquals(new String[]{"a", "b"}, user.names);

        user = gson.fromJson("{names:[\"a\"]}", User.class);
        assertArrayEquals(new String[]{"a"}, user.names);

        user = gson.fromJson("{names:\"a\"}", User.class);
        assertArrayEquals(new String[]{"a"}, user.names);
    }
}
