/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.apache.commons.cli;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CommandLineParserTest {

    private static final String[] TEST_ARGS = new String[]{"one", "-u", "url", "two"};

    @Test
    public void test1() throws ParseException {
        CommandLineParser parser = new GnuParser();

        Options options = new Options();
        options.addOption("u", "url", true, "the URL");

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("progname", options);

        CommandLine line = parser.parse(options, TEST_ARGS);

        assertTrue(line.hasOption("u"));
        assertTrue(line.hasOption("url"));
        assertEquals("url", line.getOptionValue("u"));
        assertEquals("url", line.getOptionValue("url"));

        String[] leftOver = line.getArgs();
        assertArrayEquals(new String[]{"one", "two"}, leftOver);
    }
}
