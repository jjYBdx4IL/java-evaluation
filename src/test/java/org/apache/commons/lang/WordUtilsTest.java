package org.apache.commons.lang;

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

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class WordUtilsTest {

    @Test
    public void testCapitalize() {
        assertEquals("A_a", WordUtils.capitalize("a_a"));
        assertEquals("WhatAMoron", WordUtils.capitalize("WhatAMoron"));
        assertEquals("No No No!", WordUtils.capitalize("no no no!"));
    }
}
