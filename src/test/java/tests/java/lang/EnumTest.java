package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EnumTest {

    enum Ids {

        /**
         * some desc
         */
        OPEN(100),
        // some hidden desc
        CLOSE(200);
        private int value;

        private Ids(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    enum SimpleIds {

        OPEN,
        CLOSE
    }

    @Test
    public void test() {
        assertEquals(100, Ids.OPEN.getValue());
        assertEquals(200, Ids.CLOSE.getValue());
        assertEquals("CLOSE", Ids.CLOSE.toString());
    }
}
