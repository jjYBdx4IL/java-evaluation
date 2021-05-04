package tests.java.util;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SetTest {

    private static final Logger LOG = LoggerFactory.getLogger(SetTest.class);

    class TestObj {

        public int i, j;

        TestObj(int a, int b) {
            i = a;
            j = b;
        }

        @Override
        public String toString() {
            return TestObj.class.getName() + "[" + i + "," + j + "]";
        }
    }

    class TestObjHC extends TestObj {

        TestObjHC(int a, int b) {
            super(a,b);
        }

        @Override
        public int hashCode() {
            LOG.trace(toString() + ": hashCode()");
            return i * j;
        }
    }

    class TestObjEQ extends TestObj {

        TestObjEQ(int a, int b) {
            super(a,b);
        }

        @Override
        public boolean equals(Object obj) {
            LOG.trace("equals(" + obj + ")");
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TestObj other = (TestObj) obj;
            if (other.i == i && other.j == j) {
                return true;
            }
            return false;
        }
    }

    class TestObjHCEQ extends TestObjEQ {

        TestObjHCEQ(int a, int b) {
            super(a,b);
        }

        @Override
        public int hashCode() {
            LOG.trace(toString() + ": hashCode()");
            return i * j;
        }
    }

    @Test
    public void testSetAdd() {
        Set<TestObjEQ> set = new HashSet<>();
        TestObjEQ o1 = new TestObjEQ(0, 0);
        TestObjEQ o1b = new TestObjEQ(0, 0);
        TestObjEQ o2 = new TestObjEQ(1, 0);
        TestObjEQ o3 = new TestObjEQ(3, 2);

        assertEquals(o1, o1b);

        set.add(o1);
        assertEquals(1, set.size());
        set.add(o1b);
        assertEquals(2, set.size()); // <-- "1" only with our hashCode() *and* equals() implementation!
        set.add(o2);
        assertEquals(3, set.size());
        set.add(o3);
        assertEquals(4, set.size());
    }

    @Test
    public void testSetAddEQ() {
        Set<TestObjEQ> set = new HashSet<>();
        TestObjEQ o1 = new TestObjEQ(0, 0);
        TestObjEQ o1b = new TestObjEQ(0, 0);
        TestObjEQ o2 = new TestObjEQ(1, 0);
        TestObjEQ o3 = new TestObjEQ(3, 2);

        assertEquals(o1, o1b);

        set.add(o1);
        assertEquals(1, set.size());
        set.add(o1b);
        assertEquals(2, set.size()); // <-- "1" only with our hashCode() *and* equals() implementation!
        set.add(o2);
        assertEquals(3, set.size());
        set.add(o3);
        assertEquals(4, set.size());
    }

    @Test
    public void testSetAddHC() {
        Set<TestObjHC> set = new HashSet<>();
        TestObjHC o1 = new TestObjHC(0, 0);
        TestObjHC o1b = new TestObjHC(0, 0);
        TestObjHC o2 = new TestObjHC(1, 0);
        TestObjHC o3 = new TestObjHC(3, 2);

        set.add(o1);
        assertEquals(1, set.size());
        set.add(o1b);
        assertEquals(2, set.size()); // <-- "1" only with our hashCode() *and* equals() implementation!
        set.add(o2);
        assertEquals(3, set.size());
        set.add(o3);
        assertEquals(4, set.size());
    }

    @Test
    public void testSetAddHCEQ() {
        Set<TestObjHCEQ> set = new HashSet<>();
        TestObjHCEQ o1 = new TestObjHCEQ(0, 0);
        TestObjHCEQ o1b = new TestObjHCEQ(0, 0);
        TestObjHCEQ o2 = new TestObjHCEQ(1, 0);
        TestObjHCEQ o3 = new TestObjHCEQ(3, 2);

        assertEquals(o1.hashCode(), o1b.hashCode());
        assertEquals(o1.hashCode(), o2.hashCode());
        assertNotEquals(o1.hashCode(), o3.hashCode());

        assertEquals(o1, o1b);

        set.add(o1);
        assertEquals(1, set.size());
        set.add(o1b);
        assertEquals(1, set.size()); // <-- "1" only with our hashCode() *and* equals() implementation!
        set.add(o2);
        assertEquals(2, set.size());
        set.add(o3);
        assertEquals(3, set.size());
    }
}
