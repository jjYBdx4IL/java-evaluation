package tests.enumreplacement;

import static org.junit.Assert.*;
import org.junit.Test;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
/**
 * Java enumerations are good for auto-completion inside IDEs. However, they are not type-safe in some
 * situations and are subject to the static initializer limit, ie. they cannot grow beyond some boundary. This
 * test demonstrates on how to fix those two issues by using regular classes.
 *
 * @author Github jjYBdx4IL Projects
 */
public class EnumReplacementTest {

    @Test
    public void testSimpleEnumReplacement() {
        assertEquals(1, MyEnum.i.ONE);
        assertEquals(2, MyEnum.i.TWO);
        assertEquals(3, MyEnum.i.THREE());
    }

    @Test
    public void testTypesafeEnumReplacement() {
        assertNotEquals("1", MyTypesafeEnum.i.ONE());
        assertNotEquals("2", MyTypesafeEnum.i.TWO());
        assertNotEquals("3", MyTypesafeEnum.i.THREE());
        assertEquals("1", MyTypesafeEnum.i.ONE().toString());
        assertEquals("2", MyTypesafeEnum.i.TWO().toString());
        assertEquals("3", MyTypesafeEnum.i.THREE().toString());
        assertNotEquals(MyTypesafeEnum.i.TWO(), MyTypesafeEnum.i.ONE());
        assertNotEquals(MyTypesafeEnum.i.ONE(), MyTypesafeEnum.i.TWO());
        assertEquals(MyTypesafeEnum.i.ONE(), MyTypesafeEnum.i.ONE());
        assertEquals(MyTypesafeEnum.i.TWO(), MyTypesafeEnum.i.TWO());

        assertNotEquals("1", new MyTypesafeEnum().ONE());
        assertNotEquals("2", new MyTypesafeEnum().TWO());
        assertNotEquals("3", new MyTypesafeEnum().THREE());
        assertEquals("1", new MyTypesafeEnum().ONE().toString());
        assertEquals("2", new MyTypesafeEnum().TWO().toString());
        assertEquals("3", new MyTypesafeEnum().THREE().toString());
        assertNotEquals(new MyTypesafeEnum().TWO(), new MyTypesafeEnum().ONE());
        assertNotEquals(new MyTypesafeEnum().ONE(), new MyTypesafeEnum().TWO());
        assertEquals(new MyTypesafeEnum().ONE(), new MyTypesafeEnum().ONE());
        assertEquals(new MyTypesafeEnum().TWO(), new MyTypesafeEnum().TWO());
    }
}
