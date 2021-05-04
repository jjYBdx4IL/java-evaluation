/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.awt;

import static org.junit.Assume.assumeFalse;

import org.junit.After;
import org.junit.Before;

import java.awt.GraphicsEnvironment;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ExampleJFrameBase {

    JFrame frame = null;
    JButton b1 = null;
    JButton b2 = null;
    JButton b3 = null;
    JLabel l1 = null;

    public void init() {
        frame = new JFrame("BoxLayout Test");
        b1 = new JButton("Button 1");
        b2 = new JButton("Button 2");
        b3 = new JButton("Button 3");
        l1 = new JLabel("Label 1");
    }

    @Before
    public void createExampleJFrame() {
        assumeFalse(GraphicsEnvironment.isHeadless());

        init();

        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BoxLayout boxLayout = new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS); // top
                                                                                       // to
                                                                                       // bottom
        frame.setLayout(boxLayout);

        b1 = new JButton("Button 1");
        b2 = new JButton("Button 2");
        b3 = new JButton("Button 3");
        l1 = new JLabel("Label 1");
        frame.add(b1);
        frame.add(b2);
        frame.add(b3);
        frame.add(l1);
        frame.pack();

        frame.setVisible(true);
    }

    @After
    public void after() {
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
    }
}
