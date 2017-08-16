package org.apache.log4j;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.spi.LoggingEvent;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class InterceptLogMessagesTest {

    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(InterceptLogMessagesTest.class.getName());
    private static final Logger testLogger = Logger.getLogger(InterceptLogMessagesTest.class.getName() + ".testlogger");

    private final static List<LoggingEvent> events = new ArrayList<>();

    @BeforeClass
    public static void beforeClass() {
        testLogger.setLevel(Level.ALL);
        testLogger.addAppender(new AppenderSkeleton() {

            @Override
            protected void append(LoggingEvent event) {
                events.add(event);
            }

            @Override
            public void close() {
            }

            @Override
            public boolean requiresLayout() {
                return false;
            }
        });
    }

    @Before
    public void beforeTest() {
        events.clear();
    }

    @Test
    public void test() {
        testLogger.info("test log message");
        assertEquals(1, events.size());
        assertEquals("test log message", events.get(0).getRenderedMessage());
    }

}
