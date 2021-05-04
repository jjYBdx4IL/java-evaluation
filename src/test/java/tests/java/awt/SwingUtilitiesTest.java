/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.awt;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import javax.swing.SwingUtilities;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SwingUtilitiesTest extends ExampleJFrameBase {

    @Test
    public void testGetRoot() throws InterruptedException {
        assertEquals(frame, SwingUtilities.getRoot(frame));
        assertEquals(frame, SwingUtilities.getRoot(b1));
        assertEquals(frame, SwingUtilities.getRoot(b2));
        assertEquals(frame, SwingUtilities.getRoot(b3));
        assertEquals(frame, SwingUtilities.getRoot(l1));

        assertEquals(frame.getRootPane(), SwingUtilities.getRootPane(l1));

        assertEquals(2, SwingUtilities.getRootPane(l1).getComponents().length);
        assertEquals(frame.getGlassPane(), SwingUtilities.getRootPane(l1).getComponents()[0]);
        assertEquals(frame.getLayeredPane(), SwingUtilities.getRootPane(l1).getComponents()[1]);

        assertEquals(frame.getComponents()[0], frame.getRootPane());
        assertEquals(frame.getContentPane().getComponent(0), b1);
        assertEquals(frame.getRootPane().getContentPane().getComponent(0), b1);
    }
}
