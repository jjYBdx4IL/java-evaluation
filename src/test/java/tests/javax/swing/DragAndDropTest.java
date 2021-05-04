package tests.javax.swing;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.env.Surefire;
import com.privatejgoodies.common.base.SystemUtils;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class DragAndDropTest {

    private static final Logger LOG = LoggerFactory.getLogger(DragAndDropTest.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(DragAndDropTest.class);

    public static class TestFrame extends JFrame {

        public static final String[] columnNames = new String[] { "ID", "File" };
        public final List<Integer> ids = new ArrayList<>();
        public final List<File> files = new ArrayList<>();
        public final TableModel model = new AbstractTableModel() {
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
                // rowData[row][col] = value;
                fireTableCellUpdated(row, col);
            }
        };
        public final JTable table = new JTable(model);

        class FileTransferable implements Transferable {

            public List<File> fileData = new ArrayList<>();

            /**
             * The only richer format supported is the file list flavor
             */
            protected Object getRicherData(DataFlavor flavor) {
                if (DataFlavor.javaFileListFlavor.equals(flavor)) {
                    ArrayList<Object> files = new ArrayList<Object>();
                    for (Object file : this.fileData) {
                        files.add(file);
                    }
                    return files;
                }
                return null;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                DataFlavor[] flavors = new DataFlavor[1];
                flavors[0] = DataFlavor.javaFileListFlavor;
                return flavors;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.javaFileListFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (DataFlavor.javaFileListFlavor.equals(flavor)) {
                    ArrayList<Object> files = new ArrayList<Object>();
                    for (Object file : this.fileData) {
                        files.add(file);
                    }
                    return files;
                }
                return null;
            }

        };        
        private TransferHandler handler = new TransferHandler() {
            public boolean canImport(TransferHandler.TransferSupport support) {
                if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    return false;
                }

                boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;

                if (!copySupported) {
                    return false;
                }

                support.setDropAction(COPY);

                return true;
            }

            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }

                Transferable t = support.getTransferable();

                try {
                    @SuppressWarnings("unchecked")
                    List<File> l = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

                    for (File f : l) {
                        LOG.info("dropped: " + f);
                        ids.add(model.getRowCount());
                        files.add(f);
                        model.setValueAt(null, files.size() - 1, 0);
                        model.setValueAt(null, files.size() - 1, 1);
                    }
                } catch (UnsupportedFlavorException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }

                return true;
            }

            public int getSourceActions(JComponent c) {
                LOG.info("getSourceActions");
                return COPY_OR_MOVE;
            }

            public Transferable createTransferable(JComponent c) {
                LOG.info("createTransferable");
                FileTransferable t = new FileTransferable();
                for (File f : files) {
                    t.fileData.add(f);
                }
                return t;
            }

            public void exportDone(JComponent c, Transferable t, int action) {
                LOG.info("exportDone");
                if (action == MOVE) {
                    LOG.info("drag move done");
                }
            }
        };

        public TestFrame() {
            super(DragAndDropTest.class.getSimpleName());

            table.setTransferHandler(handler);
            table.setDragEnabled(true);

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
        assumeTrue(Surefire.isSingleTestExecution());
        assumeFalse(GraphicsEnvironment.isHeadless());
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
    }

    // https://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path
    @Test
    public void testDndFiles() {
        TestFrame frame = new TestFrame();
        AWTUtils.showFrameAndWaitForCloseByUserTest(frame, new File(TEMP_DIR, "testDndFiles.png"));
    }

}
