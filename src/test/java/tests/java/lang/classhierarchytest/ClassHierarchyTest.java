package tests.java.lang.classhierarchytest;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class ClassHierarchyTest {

    @Test
    public void test() {
        assertTrue(new Dog().getThisAsString().startsWith(Dog.class.getName() + "@"));
    }
}
