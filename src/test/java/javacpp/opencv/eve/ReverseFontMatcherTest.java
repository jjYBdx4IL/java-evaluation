package javacpp.opencv.eve;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.awt.FontScanner;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.env.Surefire;
import com.github.jjYBdx4IL.utils.io.FindUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import javacpp.opencv.Common;
import javacpp.opencv.Match;
import javacpp.opencv.Template;
import testgroup.RequiresIsolatedVM;

@Category(RequiresIsolatedVM.class)
public class ReverseFontMatcherTest extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(ReverseFontMatcherTest.class);
    public static final File EVE_RES_DIR = new File(
        "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Eve Online\\SharedCache\\ResFiles");
    public static final File TEMP_DIR = Maven.getTempTestDir(ReverseFontMatcherTest.class);
    public static final File CACHE_FILE = new File(TEMP_DIR.getParentFile(), TEMP_DIR.getName() + ".resfontcache");
    public static final int PADDING = 64;
    public static final boolean INCLUDE_SYS_FONTS = false;
    private String testText = null;
    Mat screenshot = null;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void test() throws IOException, FontFormatException {
        assumeTrue(Surefire.isSingleTestExecution());

        // screenshot =
        // createScreenshot("EveOnlineBetterAutopilotLookAtMyShip.png",
        // PADDING);
        // testText = "Look At My Ship";

        screenshot = createScreenshot("EveOnlineWarpTo0m.png", PADDING);
        testText = "Warp to Location Within 0 m";

        // screenshot = createScreenshot("EveOnlineOverviewMining.png",
        // PADDING);
        // testText = "Overview (Mining)";
        // testText = "Overview";
        // testText = "(Mining)";

        List<String> fontFiles = new ArrayList<>();

        if (CACHE_FILE.exists()) {
            fontFiles = gson.fromJson(FileUtils.readFileToString(CACHE_FILE, StandardCharsets.UTF_8),
                new TypeToken<List<String>>() {
                }.getType());
        } else {
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

        for (float fontSize = 7.5f; fontSize < 14.5f; fontSize += .1f) {
            for (String fontFilePath : fontFiles) {
                File fontFile = new File(fontFilePath);
                Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                font = font.deriveFont(Font.PLAIN, fontSize);
                assertNotNull(font);

                File pngFile = writeFontExample(font, fontFile.getName(), fontSize);
                Mat tplMat = imread(pngFile.getAbsolutePath());

                Template tpl = new Template(null, tplMat, 0f, null);
                tpl.setNormalize(true);
                tpl.setBlur(true);

                Match m = tpl.findBestMatch(new Rectangle(0, 0, screenshot.cols(), screenshot.rows()), screenshot);
                LOG.info(String.format("%5.3f  %s", m.matchValue, fontFile.getAbsolutePath()));
                if (bestFile == null || m.matchValue > bestMatchValue) {
                    bestFile = fontFile;
                    bestMatchValue = m.matchValue;
                    bestFontSize = fontSize;
                }
            }
        }

        LOG.info("best match: " + bestFile.getAbsolutePath());
        LOG.info("best match value: " + bestMatchValue);
        LOG.info("best font size: " + bestFontSize);
    }

    private File writeFontExample(Font font, String outputFilename, float fontSize) throws IOException {
        BufferedImage bi = createFontImage(font, testText, 0);
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

        bi = new BufferedImage((int) (bounds.getWidth() + 2 * padding), (int) (bounds.getHeight() + 2 * padding),
            bi.getType());
        g = (Graphics2D) bi.getGraphics();
        g.setBackground(Color.black);
        g.clearRect(0, 0, bi.getWidth(), bi.getHeight());
        g.setColor(Color.white);
        g.setFont(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.drawString(text, padding + -0.3f, (float) (padding - bounds.getMinY()));
        g.dispose();
        return bi;
    }

    private Mat createScreenshot(String resId, int padding) throws IOException {
        Mat m = getResAsMat(resId);
        Mat result = new Mat(m.rows() + 2 * padding, m.cols() + 2 * padding, m.type(), new Scalar(0, 0, 0, 0));
        m.copyTo(result.apply(new Rect(padding, padding, m.cols(), m.rows())));
        return result;
    }
}
