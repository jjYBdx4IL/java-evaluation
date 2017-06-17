package org.joml;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Matrix4fTest {

    private static final Logger LOG = LoggerFactory.getLogger(Matrix4fTest.class);

    /**
     * <a href=
     * "https://github.com/lwjglgamedev/lwjglbook-bookcontents/blob/master/chapter6/chapter6.md">lwjglgamedev</a>.
     */
    @Test
    public void testPerspective() {
        Matrix4f p = new Matrix4f();
        LOG.info(System.lineSeparator() + p);
        p.perspective((float) Math.PI / 2, 2f, 1e-2f, 1e3f);
        LOG.info(System.lineSeparator() + p);
    }

    @Test
    public void testTranslation() {
        float dx = 2f;
        float dy = 3f;
        float dz = 4f;
        // arguments to Matrix4f constructor are grouped as column 4-vectors
        Matrix4f t = new Matrix4f(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, dx, dy, dz, 1f);
        LOG.info(System.lineSeparator() + t);
        Vector4f v = new Vector4f();
        LOG.info(v.toString());
        t.transform(v);
        LOG.info("translated: " + v);
        assertEquals(new Vector4f(dx, dy, dz, 1f), v);
    }

    @Test
    public void testScale() {
        float sx = 2f;
        float sy = 3f;
        float sz = 4f;
        // arguments to Matrix4f constructor are grouped as column 4-vectors
        Matrix4f s = new Matrix4f(sx, 0f, 0f, 0f, 0f, sy, 0f, 0f, 0f, 0f, sz, 0f, 0f, 0f, 0f, 1f);
        LOG.info(System.lineSeparator() + s);
        Vector4f v = new Vector4f(1f, 1f, 1f, 1f);
        LOG.info(v.toString());
        s.transform(v);
        LOG.info("scaled: " + v);
        assertEquals(new Vector4f(sx, sy, sz, 1f), v);
    }

    @Test
    public void testRotation() {
        Matrix4f r = new Matrix4f();
        r.rotate((float) Math.PI, new Vector3f(0f, 0f, 1f).normalize());
        LOG.info(System.lineSeparator() + r);
        assertVectorEquals(new Vector4f(-1f, 0f, 0f, 1f), r.transform(new Vector4f(1f, 0f, 0f, 1f)));
    }
    
    public static void assertVectorEquals(Vector4f v1, Vector4f v2) {
        final float prec = 1e-6f;
        assertEquals(v1.get(0), v2.get(0), prec);
        assertEquals(v1.get(1), v2.get(1), prec);
        assertEquals(v1.get(2), v2.get(2), prec);
        assertEquals(v1.get(3), v2.get(3), prec);
    }
}
