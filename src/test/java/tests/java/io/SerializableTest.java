package tests.java.io;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings("serial")
public class SerializableTest implements Serializable {

    private class Test1 implements Serializable {
        private final int a;
        Test1(int _a) {
            a = _a;
        }
        public int getA() {
            return a;
        }
    }

    @Test
    public void test1() {
        testSerialization(123, new Test1(123));
    }

    private static void testSerialization(int testValue, Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(obj);
            }
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
                Test1 test = (Test1) ois.readObject();
                assertNotNull(test);
                assertEquals(testValue, test.getA());
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
