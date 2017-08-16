/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.awt;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;

import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class MultiscreenTest {

    private static final Logger LOG = LoggerFactory.getLogger(MultiscreenTest.class);

    @BeforeClass
    public static void beforeClass() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    }

    @Test
    public void determineActiveScreenTest() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                LOG.info(ge.getDefaultScreenDevice().getIDstring());

                LOG.info(MouseInfo.getPointerInfo().getDevice().toString());

                JFrame jf = new JFrame("title");
                jf.setAlwaysOnTop(true);
                //jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JLabel label = new JLabel("test");
                jf.add(label);
                jf.pack();
                AWTUtils.centerOnMouseScreen(jf);
                jf.setVisible(true);
            }
        });
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        });
    }
}
