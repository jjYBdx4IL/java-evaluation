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
import java.io.NotSerializableException;
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

    interface GetA {

        public int getA();
    }

    private class ClassImplSerializable implements Serializable, GetA {

        private final int a;

        ClassImplSerializable(int _a) {
            a = _a;
        }

        @Override
        public int getA() {
            return a;
        }
    }

    @Test
    public void test1() throws Exception {
        testSerialization(123, new ClassImplSerializable(123));
    }

    private class ClassNotImplSerializable implements GetA {

        private final int a;

        ClassNotImplSerializable(int _a) {
            a = _a;
        }

        @Override
        public int getA() {
            return a;
        }
    }

    @Test(expected = NotSerializableException.class)
    public void test2() throws Exception {
        testSerialization(123, new ClassNotImplSerializable(123));
    }

    private class ClassImplSerializableWithInnerClass implements Serializable, GetA {

        protected final InnerClass innerClass;

        ClassImplSerializableWithInnerClass(int _a) {
            innerClass = new InnerClass(_a);
        }

        @Override
        public int getA() {
            return innerClass.getA();
        }

        public class InnerClass {

            protected final int a;

            InnerClass(int _a) {
                a = _a;
            }

            public int getA() {
                return a;
            }
        }
    }

    /**
     * Serializable gets inherited by sub-classes but not by inner classes!
     * 
     * @throws Exception 
     */
    @Test(expected = NotSerializableException.class)
    public void test3() throws Exception {
        testSerialization(123, new ClassImplSerializableWithInnerClass(123));
    }

    private static Object testSerialization(int testValue, GetA obj) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(obj);
            }
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
                GetA test = (GetA) ois.readObject();
                assertNotNull(test);
                assertEquals(testValue, test.getA());
                return test;
            }
        }
    }
}
