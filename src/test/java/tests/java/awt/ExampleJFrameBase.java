/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.awt;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.junit.After;
import org.junit.Before;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ExampleJFrameBase {

    JFrame frame = new JFrame("BoxLayout Test");
    JButton b1 = new JButton("Button 1");
    JButton b2 = new JButton("Button 2");
    JButton b3 = new JButton("Button 3");
    JLabel l1 = new JLabel("Label 1");

    @Before
    public void createExampleJFrame() {
        frame = new JFrame("BoxLayout Test");

        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BoxLayout boxLayout = new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS); // top to bottom
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
        frame.setVisible(false);
        frame.dispose();
    }
}
