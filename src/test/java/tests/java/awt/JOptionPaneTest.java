/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.awt;

import com.github.jjYBdx4IL.utils.env.Surefire;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import static org.junit.Assert.fail;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class JOptionPaneTest {

    private static final Logger LOG = LoggerFactory.getLogger(JOptionPaneTest.class);

    @Before
    public void before() {
        Assume.assumeTrue(Surefire.isSingleTestExecution());
    }

    /**
     * always shows the dialog on the primary screen....
     */
    @Test
    public void testShowConfirmDialog() {
        int dialogResult = JOptionPane.showConfirmDialog(null, "Your Message", "Title on Box", JOptionPane.YES_NO_OPTION);
        switch (dialogResult) {
            case JOptionPane.YES_OPTION:
                LOG.info("yes pressed");
                break;
            case JOptionPane.NO_OPTION:
                LOG.info("no pressed");
                break;
            default:
                fail();
        }
    }

    @Test
    public void testCreateDialog() {
        JOptionPane jOptionPane = new JOptionPane("Really do this?", JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION);
        JDialog jDialog = jOptionPane.createDialog("dialog title");
        jDialog.setVisible(true);

        Object selectedValue = jOptionPane.getValue();
        int dialogResult = JOptionPane.CLOSED_OPTION;
        if (selectedValue != null) {
            dialogResult = Integer.parseInt(selectedValue.toString());
        }

        switch (dialogResult) {
            case JOptionPane.YES_OPTION:
                LOG.info("yes pressed");
                break;
            case JOptionPane.NO_OPTION:
                LOG.info("no pressed");
                break;
            case JOptionPane.CLOSED_OPTION:
                LOG.info("closed");
                break;
            default:
                fail();
        }
    }
}
