package tests.enums;

import org.junit.Test;
import static org.junit.Assert.*;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class EnumWithMultipleConstructorsTest {

    @Test
    public void test() {
        assertEquals("ONE", EnumWithMultipleConstructors.ONE.toString());
        assertEquals("2", EnumWithMultipleConstructors.TWO.toString());
    }
}
