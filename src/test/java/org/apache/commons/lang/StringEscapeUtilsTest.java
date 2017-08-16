package org.apache.commons.lang;

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
 *
 * @author Github jjYBdx4IL Projects
 */
public class StringEscapeUtilsTest {

    @Test
    public void testHtml() {
        assertEquals("\n", StringEscapeUtils.escapeHtml("\n"));
        assertEquals("&lt;&gt;", StringEscapeUtils.escapeHtml("<>"));
        //System.out.println(StringEscapeUtils.escapeHtml(""));
    }
}
