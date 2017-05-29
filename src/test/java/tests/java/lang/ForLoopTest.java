package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ForLoopTest {

    @Test
    public void testInc() {
        int i;
        for (i = 0; i < 1; i++) {
            assertEquals(0, i);
        }
        assertEquals(1, i);

        for (i = 0; i < 1; ++i) {
            assertEquals(0, i);
        }
        assertEquals(1, i);
    }

    @SuppressWarnings("unused")
	@Test
    public void testNestedWithLabels() {
        int i, j = 0;
        outer:
        for (i = 0; i < 2; i++) {
            inner:
            for (j = 0; j < 2; j++) {
                continue outer;
            }
        }
        assertEquals(2, i);
        assertEquals(0, j);
    }

    @SuppressWarnings("unused")
	@Test
    public void testContinue() {
        int i;
        for (i = 0; i < 1; i++) {
            continue;
        }
        assertEquals(1, i);

        // continue via label on same loop
        sameLoop:
        for (i = 0; i < 1; i++) {
            continue sameLoop;
        }
        assertEquals(1, i);

        // continue via label on outer loop
        outerLoop:
        for (int j = 0; j < 1; j++) {
            for (i = 0; i < 1; i++) {
                continue outerLoop;
            }
        }
        assertEquals(0, i);
    }

    @SuppressWarnings("unused")
	@Test
    public void testBreak() {
        int i, j;
        for (i = 0; i < 10; i++) {
            break;
        }
        assertEquals(0, i);

        // continue via label on same loop
        sameLoop:
        for (i = 0; i < 10; i++) {
            break sameLoop;
        }
        assertEquals(0, i);

        // continue via label on outer loop
        outerLoop:
        for (j = 0; j < 1; j++) {
            for (i = 0; i < 1; i++) {
                break outerLoop;
            }
        }
        assertEquals(0, i);
        assertEquals(0, j);
    }

    @Test
    public void testFinalValue() {
        int x;
        for(x = 0; x < 10; x++) {
        }
        assertEquals(10, x);
    }

    @Test
    public void testIfReplacement() {
        int i;
        for(i = 0; returnBoolean(false); i++) {
            fail();
        }
        assertEquals(0, i);
    }

    private static boolean returnBoolean(boolean arg) {
        return arg;
    }
}
