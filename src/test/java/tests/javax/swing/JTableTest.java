package tests.javax.swing;

import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;

import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class JTableTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(JTableTest.class);

    public static class TestFrame extends JFrame {

        public static final String[] columnNames = new String[] { "ID", "File" };
        public List<Integer> ids = new ArrayList<>();
        public List<File> files = new ArrayList<>();
        public TableModel model = new AbstractTableModel() {
            public String getColumnName(int col) {
                return columnNames[col].toString();
            }

            public int getRowCount() {
                return files.size();
            }

            public int getColumnCount() {
                return columnNames.length;
            }

            public Object getValueAt(int row, int col) {
                return col == 0 ? ids.get(row) : files.get(row);
            }

            public boolean isCellEditable(int row, int col) {
                return false;
            }

            public void setValueAt(Object value, int row, int col) {
                //rowData[row][col] = value;
                fireTableCellUpdated(row, col);
            }
        };
        public JTable table = new JTable(model);

        public TestFrame() {
            super(JTableTest.class.getSimpleName());

            ids.add(0);
            files.add(new File("C:\\some\\test\\File"));
            ids.add(1);
            files.add(new File("C:\\another\\test\\File"));
            
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.weighty = 1;
            add(table, c);

            setPreferredSize(new Dimension(800, 600));
            pack();
        }
    }

    @Before
    public void before() {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }

    @Test
    public void testJTableAndModel() {
        TestFrame frame = new TestFrame();
        AWTUtils.showFrameAndWaitForCloseByUserTest(frame, new File(TEMP_DIR, "test.png"));
        // @insert:image:test.png@
    }

}
