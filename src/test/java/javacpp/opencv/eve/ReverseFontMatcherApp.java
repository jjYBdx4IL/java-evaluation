package javacpp.opencv.eve;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.awt.FontScanner;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.io.FindUtils;
import com.github.jjYBdx4IL.utils.remoterobot.RobotClient;
import com.github.jjYBdx4IL.utils.text.Eta;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.ui.Drawable;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javacpp.opencv.Common;
import javacpp.opencv.Match;
import javacpp.opencv.Template;
import testgroup.RequiresIsolatedVM;

@Category(RequiresIsolatedVM.class)
public class ReverseFontMatcherApp extends Common implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ReverseFontMatcherApp.class);
    public static final File EVE_RES_DIR = new File(Utils.sharedCacheLoc + "/ResFiles");
    public static final File TEMP_DIR = Maven.getTempTestDir(ReverseFontMatcherApp.class);
    public static final File CACHE_FILE = new File(TEMP_DIR.getParentFile(), TEMP_DIR.getName() + ".resfontcache");
    public static final int PADDING = 64;
    public static final boolean INCLUDE_SYS_FONTS = false;
    private String testText = null;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    Mat inputImage = null;
    BufferedImage inputImageBi = null;
    JProgressBar progressBar = null;
    float minFontSize = 8;
    float maxFontSize = 14;
    float stepFontSize = 0.1f;

    static class ScreenshotPanel extends JPanel implements MouseListener, MouseMotionListener {
        private static final long serialVersionUID = 1L;
        BufferedImage bi = null;
        Rectangle selection = new Rectangle();
        JTextField coordsLabel;

        ScreenshotPanel(JTextField coordsLabel) {
            super();
            this.coordsLabel = coordsLabel;
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(bi, 0, 0, getWidth() - 1, getHeight() - 1, 0, 0, bi.getWidth() - 1, bi.getHeight() - 1,
                null);
            g.setColor(Color.red);
            Rectangle rn = normalize(selection);
            fromPicCoords(rn);
            g.drawRect(rn.x, rn.y, rn.width, rn.height);
        }

        Rectangle normalize(Rectangle r) {
            Rectangle rout = new Rectangle(r);
            if (r.width < 0) {
                rout.x = r.x + r.width;
                rout.width = -r.width;
            }
            if (r.height < 0) {
                rout.y = r.y + r.height;
                rout.height = -r.height;
            }
            return rout;
        }

        private void fromPicCoords(Rectangle r) {
            r.x = fromPicX(r.x);
            r.y = fromPicY(r.y);
            r.width = fromPicX(r.width);
            r.height = fromPicY(r.height);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            selection.x = toPicX(e.getX());
            selection.y = toPicY(e.getY());
            selection.width = 0;
            selection.height = 0;
            updateCoordsLabel();
            repaint();
        }

        private int fromPicX(int x) {
            return x * getWidth() / bi.getWidth();
        }

        private int fromPicY(int y) {
            return y * getHeight() / bi.getHeight();
        }

        private int toPicX(int x) {
            return x * bi.getWidth() / getWidth();
        }

        private int toPicY(int y) {
            return y * bi.getHeight() / getHeight();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            selection.width = toPicX(e.getX()) - selection.x;
            selection.height = toPicY(e.getY()) - selection.y;
            updateCoordsLabel();
            repaint();
        }

        private void updateCoordsLabel() {
            Rectangle r = normalize(selection);
            coordsLabel.setText(String.format(Locale.ROOT, "%d, %d, %d, %d", r.x, r.y, r.width, r.height));
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            selection.width = toPicX(e.getX()) - selection.x;
            selection.height = toPicY(e.getY()) - selection.y;
            updateCoordsLabel();
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    static class AppWindow extends JFrame implements WindowListener, ActionListener {
        private static final long serialVersionUID = 1L;

        JTextField coordsLabel = new JTextField("");
        JButton updateSsButton = new JButton("Update Screenshot");
        ScreenshotPanel screenshotPanel = new ScreenshotPanel(coordsLabel);

        JLabel minFontSizeL = new JLabel("min font size:");
        JLabel maxFontSizeL = new JLabel("max font size:");
        JLabel stepFontSizeL = new JLabel("step font size:");
        JLabel matchTextL = new JLabel("match text:");

        JTextField minFontSize = new JTextField("8");
        JTextField maxFontSize = new JTextField("14");
        JTextField stepFontSize = new JTextField("0.1");
        JTextField matchText = new JTextField("");
        JButton matchStartButton = new JButton("start");

        JProgressBar progressBar = new JProgressBar(0, 100);

        CountDownLatch windowClosed = new CountDownLatch(1);
        RobotClient bot = null;

        AppWindow(RobotClient bot) {
            super(ReverseFontMatcherApp.class.getSimpleName());
            this.bot = bot;
            getContentPane().setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            // c.weightx = 1f / 3;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0;
            c.weighty = 0;
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = GridBagConstraints.REMAINDER;
            getContentPane().add(updateSsButton, c);

            c.weighty = 0.5;
            c.fill = GridBagConstraints.BOTH;
            c.gridx = 0;
            c.gridy++;
            getContentPane().add(screenshotPanel, c);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy++;
            getContentPane().add(coordsLabel, c);

            c.gridy++;
            c.gridx = 0;
            c.gridwidth = 1;
            getContentPane().add(minFontSizeL, c);
            c.gridx++;
            getContentPane().add(maxFontSizeL, c);
            c.gridx++;
            getContentPane().add(stepFontSizeL, c);
            c.gridx++;
            getContentPane().add(matchTextL, c);

            c.gridx = 0;
            c.weightx = 0.1;
            c.gridy++;
            c.gridwidth = 1;
            getContentPane().add(minFontSize, c);
            c.gridx++;
            getContentPane().add(maxFontSize, c);
            c.gridx++;
            getContentPane().add(stepFontSize, c);
            c.weightx = 1;
            c.gridx++;
            getContentPane().add(matchText, c);
            c.weightx = 0.1;
            c.gridx++;
            c.gridwidth = GridBagConstraints.REMAINDER;
            getContentPane().add(matchStartButton, c);

            c.weightx = 0;
            c.gridx = 0;
            c.gridy++;
            getContentPane().add(progressBar, c);

            updateSsButton.addActionListener(this);
            matchStartButton.addActionListener(this);
            addWindowListener(this);

            coordsLabel.setFont(new Font("Monospace", Font.PLAIN, 16));
            coordsLabel.setEditable(false);

            progressBar.setStringPainted(true);

            pack();
        }

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent e) {
            dispose();
        }

        @Override
        public void windowClosed(WindowEvent e) {
            windowClosed.countDown();
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }

        public void updateScreenshot() throws IOException {
            screenshotPanel.bi = bot.createScreenCapture(null);
            screenshotPanel
                .setPreferredSize(new Dimension(screenshotPanel.bi.getWidth(), screenshotPanel.bi.getHeight()));
            pack();
            repaint();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == updateSsButton) {
                try {
                    updateScreenshot();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else if (e.getSource() == matchStartButton) {
                ReverseFontMatcherApp matcher = new ReverseFontMatcherApp();
                BufferedImage image = screenshotPanel.bi;
                Rectangle r = screenshotPanel.normalize(screenshotPanel.selection);
                if (r.width > 0 && r.height > 0) {
                    // Mat conversion seems to have problems with odd dimension sizes
                    r.width += r.width % 2;
                    r.height += r.height % 2;
                    if (r.width > screenshotPanel.bi.getWidth()) {
                        r.width = screenshotPanel.bi.getWidth() / 2 * 2;
                    }
                    if (r.height > screenshotPanel.bi.getHeight()) {
                        r.width = screenshotPanel.bi.getHeight() / 2 * 2;
                    }
                    if (r.x + r.width > screenshotPanel.bi.getWidth()) {
                        r.x = screenshotPanel.bi.getWidth() - r.width;
                    }
                    if (r.y + r.height > screenshotPanel.bi.getHeight()) {
                        r.y = screenshotPanel.bi.getHeight() - r.height;
                    }
                    image = new BufferedImage(r.width, r.height, image.getType());
                    screenshotPanel.bi.getSubimage(r.x, r.y, r.width, r.height).copyData(image.getRaster());
                }
                matcher.inputImageBi = image;
                matcher.progressBar = progressBar;
                matcher.minFontSize = Float.parseFloat(minFontSize.getText());
                matcher.maxFontSize = Float.parseFloat(maxFontSize.getText());
                matcher.stepFontSize = Float.parseFloat(stepFontSize.getText());
                matcher.testText = matchText.getText();
                Thread t = new Thread(matcher);
                t.start();
            }
        }
    }

    static class ImagePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        BufferedImage bi;

        ImagePanel(BufferedImage bi) {
            super();
            this.bi = bi;
            setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
        }

        ImagePanel(File imageFile) throws IOException {
            this(ImageIO.read(imageFile));
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(bi, 0, 0, getWidth() - 1, getHeight() - 1, 0, 0, bi.getWidth() - 1, bi.getHeight() - 1,
                null);
        }
    }

    static class JFreeDrawablePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        Drawable chart;

        JFreeDrawablePanel(Drawable chart) {
            super();
            this.chart = chart;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            chart.draw((Graphics2D) g, g.getClipBounds());
        }
    }

    static class ResultWindow extends JFrame {
        private static final long serialVersionUID = 1L;

        String desc;
        File fontExamplePng;
        BufferedImage inputImage;
        JFreeChart chart;

        ResultWindow() {
            super("ResultsWindow");
        }

        void update() throws IOException {
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            c.weightx = 0.5;
            c.weighty = 0;

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            getContentPane().add(new JLabel("Input image:"), c);

            c.weighty = 0.5;
            c.fill = GridBagConstraints.BOTH;
            c.gridy++;
            getContentPane().add(new ImagePanel(inputImage), c);

            c.weighty = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridy++;
            getContentPane().add(new JLabel("Reconstructed image using best font match:"), c);

            c.weighty = 0.5;
            c.fill = GridBagConstraints.BOTH;
            c.gridy++;
            getContentPane().add(new ImagePanel(fontExamplePng), c);

            c.weighty = 0;
            c.fill = GridBagConstraints.BOTH;
            c.gridy++;
            JTextArea textArea = new JTextArea(desc);
            textArea.setEditable(false);
            getContentPane().add(textArea, c);

            c.weighty = 0.5;
            c.fill = GridBagConstraints.BOTH;
            c.gridy++;
            getContentPane().add(new JFreeDrawablePanel(chart), c);

            setMinimumSize(new Dimension(1024, 768));

            pack();
        }

    }

    public static void main(String[] args) throws Exception {
        new ReverseFontMatcherApp().runMain();
    }

    public void runMain() throws Exception {

        enableBot();

        AppWindow win = new AppWindow(bot);
        win.updateScreenshot();
        AWTUtils.centerOnMouseScreen(win);
        win.setVisible(true);
        win.windowClosed.await();

    }

    @Override
    public void run() {
        try {
            run2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run2() throws Exception {

        inputImage = toMat(inputImageBi);

        List<String> fontFiles = new ArrayList<>();

        if (CACHE_FILE.exists()) {
            progressBar.setString("loading font list from cache...");
            fontFiles = gson.fromJson(FileUtils.readFileToString(CACHE_FILE, StandardCharsets.UTF_8),
                new TypeToken<List<String>>() {
                }.getType());
        } else {
            progressBar.setString("searching for fonts...");
            for (File candidate : FindUtils.globFiles(EVE_RES_DIR, "**")) {
                try {
                    Font.createFont(Font.TRUETYPE_FONT, candidate);
                    fontFiles.add(candidate.getAbsolutePath());
                    LOG.info("found eve font file: " + candidate.getAbsolutePath());
                } catch (FontFormatException ex) {
                }
                try {
                    Font.createFont(Font.TYPE1_FONT, candidate);
                    fontFiles.add(candidate.getAbsolutePath());
                    LOG.info("found eve font file: " + candidate.getAbsolutePath());
                } catch (FontFormatException ex) {
                }
            }
            FileUtils.writeStringToFile(CACHE_FILE, gson.toJson(fontFiles,
                new TypeToken<List<String>>() {
                }.getType()), StandardCharsets.UTF_8);
        }

        progressBar.setString("searching for system fonts...");
        if (INCLUDE_SYS_FONTS) {
            FontScanner fontScanner = new FontScanner();
            List<String> files = fontScanner.getFontFiles(
                SystemUtils.IS_OS_WINDOWS ? "C:\\Windows\\fonts" : "/usr/share/fonts");
            assertFalse(files.isEmpty());

            for (String fontFileName : files) {
                fontFiles.add(fontFileName);
            }
        }

        File bestFile = null;
        float bestMatchValue = Float.MIN_VALUE;
        float bestFontSize = Float.MIN_VALUE;
        File bestFontExample = null;

        int nFontSizes = (int) Math.floor((maxFontSize - minFontSize) / stepFontSize) + 1;
        int iterationsTotal = nFontSizes * fontFiles.size();
        Eta<Integer> eta = new Eta<>(0, iterationsTotal, 1000L);
        progressBar.setMaximum(iterationsTotal);
        int n = 0;
        List<Float> matchValues = new ArrayList<>();
        for (int i = 0; i <= nFontSizes; i++) {
            float fontSize = minFontSize + i * stepFontSize;
            LOG.info("font size = " + fontSize);
            for (String fontFilePath : fontFiles) {
                String progress = eta.toStringPeriodical(n);
                if (progress != null) {
                    progressBar.setString(String.format("%s, best match = %.4f", progress, bestMatchValue));
                    progressBar.setValue(n);
                }
                n++;

                File fontFile = new File(fontFilePath);
                Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                font = font.deriveFont(Font.PLAIN, fontSize);
                assertNotNull(font);

                File pngFile = writeFontExample(font, n + "-" + fontFile.getName(), fontSize);
                Mat tplMat = imread(pngFile.getAbsolutePath());

                Template tpl = new Template(null, tplMat, 0f, null);
                tpl.setNormalize(true);
                tpl.setBlur(true);

                Match m = tpl.findBestMatch(new Rectangle(0, 0, inputImage.cols(), inputImage.rows()), inputImage);
                if (m != null) {
                    LOG.info(String.format("%5.3f  %s", m.matchValue, fontFile.getAbsolutePath()));
                    if (bestFile == null || m.matchValue > bestMatchValue) {
                        bestFile = fontFile;
                        bestMatchValue = m.matchValue;
                        bestFontSize = fontSize;
                        bestFontExample = pngFile;
                    }
                    matchValues.add(m.matchValue);
                }
            }
        }
        progressBar.setString(String.format("%s, best match = %.4f", eta.toString(n), bestMatchValue));
        progressBar.setValue(n);

        LOG.info("best match: " + bestFile.getAbsolutePath());
        LOG.info("best match value: " + bestMatchValue);
        LOG.info("best font size: " + bestFontSize);

        StringBuilder sb = new StringBuilder();
        sb.append("Match value: " + bestMatchValue + System.lineSeparator());
        sb.append("Font size: " + bestFontSize + System.lineSeparator());
        sb.append("Font: " + bestFile.getAbsolutePath() + System.lineSeparator());

        double[] values = new double[matchValues.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = matchValues.get(i);
        }

        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("distribution of gaussian random generator", values, (int) Math.sqrt(values.length));
        JFreeChart chart = ChartFactory.createHistogram(
            "distribution of match values across all fonts and font sizes",
            "match value",
            "frequency",
            dataset,
            PlotOrientation.VERTICAL,
            false, false, false);

        ResultWindow resWin = new ResultWindow();
        resWin.fontExamplePng = bestFontExample;
        resWin.desc = sb.toString();
        resWin.inputImage = inputImageBi;
        resWin.chart = chart;
        resWin.update();
        resWin.setVisible(true);
    }

    private File writeFontExample(Font font, String outputFilename, float fontSize) throws IOException {
        BufferedImage bi = createFontImage(font, testText, 1);
        File out = new File(TEMP_DIR, String.format(Locale.ROOT, "%s.%.1f.png", outputFilename, fontSize));
        ImageIO.write(bi, "png", out);
        return out;
    }

    public static BufferedImage createFontImage(Font baseFont, int fontStyle, float fontSize, String text,
        int padding) {
        Font font = baseFont.deriveFont(fontStyle, fontSize);
        return createFontImage(font, text, padding);
    }

    public static BufferedImage createFontImage(Font font, String text, int padding) {
        BufferedImage bi = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        FontMetrics fm = g.getFontMetrics(font);
        Rectangle2D bounds = fm.getStringBounds(text, g);
        g.dispose();

        int width = (int) Math.ceil(bounds.getWidth() + 2 * padding);
        int height = (int) Math.ceil(bounds.getHeight() + 2 * padding); 
        
        bi = new BufferedImage(width + width % 2, height + height % 2, bi.getType());
        g = (Graphics2D) bi.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setBackground(Color.black);
        g.setColor(Color.white);
        g.setFont(font);
        g.clearRect(0, 0, bi.getWidth(), bi.getHeight());
        g.drawString(text, padding, padding + fm.getAscent());
        g.dispose();
        return bi;
    }
}
