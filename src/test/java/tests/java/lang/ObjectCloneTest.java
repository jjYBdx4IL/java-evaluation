package tests.java.lang;
/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.junit.Test;
import static org.junit.Assert.*;

public class ObjectCloneTest {
    enum TestEnum {
        ONE, TWO;
    }
    
    class TestClass implements Cloneable {
        public TestEnum testEnum;
        public String someAttr;
        public TestClass clone() throws CloneNotSupportedException {
            return (TestClass) super.clone();
        }
    }
    
    @Test
    public void test1() throws CloneNotSupportedException {
        TestClass tc = new TestClass();
        tc.someAttr = "123";
        tc.testEnum = TestEnum.ONE;
        TestClass tc2 = (TestClass) tc.clone();
        assertEquals("123", tc2.someAttr);
        assertEquals(TestEnum.ONE, tc2.testEnum);
    }
}
