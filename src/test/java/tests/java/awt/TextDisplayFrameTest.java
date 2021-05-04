package tests.java.awt;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextDisplayFrameTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(TextDisplayFrameTest.class);
    private static final Logger LOG = LoggerFactory.getLogger(TextDisplayFrameTest.class);

    @SuppressWarnings("serial")
    class TextDisplayFrame extends JFrame {

        public TextDisplayFrame(String textToShow) {
            // getContentPane().setLayout(new FlowLayout());

            JTextArea textArea = new JTextArea();
            textArea.setText(textToShow);
            textArea.setFont(new Font("Monospaced", Font.BOLD, 16));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            getContentPane().add(scrollPane);

            textArea.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                    LOG.info(e + "");
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    LOG.info(e + "");
                    // don't close window when using scroll keys
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_Q) {
                        TextDisplayFrame.this
                            .dispatchEvent(new WindowEvent(TextDisplayFrame.this, WindowEvent.WINDOW_CLOSING));
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    LOG.info(e + "");
                }
            });
        }
    }

    @BeforeClass
    public static void beforeClass() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    }

    @Test
    public void test() throws InvocationTargetException, InterruptedException, IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("line " + i + "\n");
        }

        final JFrame frame = new TextDisplayFrame(sb.toString());
        frame.setAutoRequestFocus(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        AWTUtils.centerOnMouseScreen(frame);
        frame.setVisible(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        AWTUtils.writeToPng(frame, new File(TEMP_DIR, "TextDisplayFrameTest.png"));
        // @insert:image:TextDisplayFrameTest.png@
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });
    }
}
