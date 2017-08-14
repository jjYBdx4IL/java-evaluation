package com.nothome.delta;

import static org.junit.Assert.*;

import org.junit.Test;

public class DeltaTest {

    @Test
    public void test() throws Exception {
        final String v1 = "text no 1";
        final String v2 = "text no 2";
        byte[] patch = new Delta().compute(v1.getBytes("UTF-8"), v2.getBytes("UTF-8"));
        byte[] result = new GDiffPatcher().patch(v1.getBytes("UTF-8"), patch);
        assertEquals(v2, new String(result, "UTF-8"));
    }
}
