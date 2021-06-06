package org.json;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class OrgJSONTest {

    JSONObject o;

    @Before
    public void before() {
        o = new JSONObject();
    }

    @Test
    public void testJSONObjectPut() {
        o.put("key", "value");
        assertEquals("{\"key\":\"value\"}", o.toString());
    }

    @Test
    public void testJSONObjectAppend() {
        o.append("key", "value");
        assertEquals("{\"key\":[\"value\"]}", o.toString());
    }

    @Test
    public void testJSONDecode() {
        o = new JSONObject("{\"key\":\"value\"}");
        assertEquals("value", o.get("key"));
    }
}
