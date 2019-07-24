package gui.glazedlists;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;
import org.junit.Test;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * inspired by: https://publicobject.com/glazedlistsdeveloper/
 * 
 *
 */
public class BasicEventListTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(BasicEventListTest.class);
    
    public static class AmericanIdol {
        private String name;
        private int votes;

        public AmericanIdol(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getVotes() {
            return votes;
        }

        public void setVotes(int votes) {
            this.votes = votes;
        }

        public void incrementVotes() {
            this.votes++;
        }
    }

    @Test
    public void test() throws Exception {
        // create an EventList of AmericanIdols
        EventList<AmericanIdol> idols = GlazedLists.threadSafeList(new BasicEventList<AmericanIdol>());
        idols.add(new AmericanIdol("Simon Cowell"));
        idols.add(new AmericanIdol("Paula Abdul"));
        idols.add(new AmericanIdol("Randy Jackson"));
        idols.add(new AmericanIdol("Ryan Seacrest"));

        // build a JTable
        String[] propertyNames = {"name", "votes"};
        String[] columnLabels = {"Name", "Votes"};
        TableFormat<AmericanIdol> tf = GlazedLists.tableFormat(AmericanIdol.class, propertyNames, columnLabels);
        JTable t = new JTable(new DefaultEventTableModel<AmericanIdol>(idols, tf));

        // place the table in a JFrame
        JFrame f = new JFrame();
        f.add(new JScrollPane(t), BorderLayout.CENTER);

        // show the frame
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        AWTUtils.writeToPng(f.getContentPane(), new File(TEMP_DIR, "test1.png"));
        //@insert:image:test1.png@
        
        idols.get(0).setVotes(123);
        
        AWTUtils.writeToPng(f.getContentPane(), new File(TEMP_DIR, "test2.png"));
        //@insert:image:test2.png@
        
        AWTUtils.showFrameAndWaitForCloseByUserTest(f);
    }
}