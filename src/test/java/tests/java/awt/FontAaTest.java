package tests.java.awt;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.junit.Test;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FontAaTest extends InteractiveTestBase {

    private static final File TEMP_DIR = Maven.getTempTestDir(FontAaTest.class);
    final Font f = new Font("Helvetica", Font.PLAIN, 24);
    String text = "Don't go there!";

    // only text-aa has an effect on g.drawString
    @Test
    public void testAaUsingG2dDrawString() throws IOException {
        writeTextImageG2dDrawString(RenderingHints.VALUE_ANTIALIAS_OFF,
            RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
            new File(TEMP_DIR, "testAA_off_off.png"));
        // @insert:image:testAA_off_off.png@

        writeTextImageG2dDrawString(RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
            new File(TEMP_DIR, "testAA_on_off.png"));
        // @insert:image:testAA_on_off.png@

        writeTextImageG2dDrawString(RenderingHints.VALUE_ANTIALIAS_OFF,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
            new File(TEMP_DIR, "testAA_off_on.png"));
        // @insert:image:testAA_off_on.png@

        writeTextImageG2dDrawString(RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
            new File(TEMP_DIR, "testAA_on_on.png"));
        // @insert:image:testAA_on_on.png@

        writeTextImageG2dDrawString(RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.VALUE_TEXT_ANTIALIAS_GASP,
            new File(TEMP_DIR, "testAA_on_gasp.png"));
        // @insert:image:testAA_on_gasp.png@
    }

    private void writeTextImageG2dDrawString(Object aaHint, Object textAaHint, File outFile) throws IOException {

        BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(text, g);
        g.dispose();

        int padding = 1;
        img = new BufferedImage((int) Math.ceil(r.getWidth() + 2 * padding),
            (int) Math.ceil(fm.getHeight() + 2 * padding),
            BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) img.getGraphics();
        g.setFont(f);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aaHint);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, textAaHint);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setBackground(Color.black);
        g.setColor(Color.white);
        g.clearRect(0, 0, img.getWidth(), img.getHeight());
        // there is no floating point (sub-pixel) precision here:
        g.drawString(text, (float) padding + 0.0f, (float) padding + fm.getAscent());
        g.dispose();

        img = Scalr.resize(img, Method.SPEED, img.getWidth() * 10, img.getHeight() * 10);

        ImageIO.write(img, "png", outFile);
    }

    @Test
    public void testAaUsingShapes() throws IOException {
        writeTextImageUsingShapes(RenderingHints.VALUE_ANTIALIAS_OFF,
            RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
            new File(TEMP_DIR, "testAAS_off_off.png"));
        // @insert:image:testAAS_off_off.png@

        writeTextImageUsingShapes(RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
            new File(TEMP_DIR, "testAAS_on_off.png"));
        // @insert:image:testAAS_on_off.png@

        writeTextImageUsingShapes(RenderingHints.VALUE_ANTIALIAS_OFF,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
            new File(TEMP_DIR, "testAAS_off_on.png"));
        // @insert:image:testAAS_off_on.png@

        writeTextImageUsingShapes(RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
            new File(TEMP_DIR, "testAAS_on_on.png"));
        // @insert:image:testAAS_on_on.png@

        writeTextImageUsingShapes(RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.VALUE_TEXT_ANTIALIAS_GASP,
            new File(TEMP_DIR, "testAAS_on_gasp.png"));
        // @insert:image:testAAS_on_gasp.png@
    }

    private void writeTextImageUsingShapes(Object aaHint, Object textAaHint, File outFile) throws IOException {
        BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(text, g);
        g.dispose();

        int padding = 1;
        img = new BufferedImage((int) Math.ceil(r.getWidth() + 2 * padding),
            (int) Math.ceil(fm.getHeight() + 2 * padding),
            BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) img.getGraphics();
        g.setFont(f);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aaHint);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, textAaHint);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setBackground(Color.black);
        g.setColor(Color.white);
        g.clearRect(0, 0, img.getWidth(), img.getHeight());
        TextLayout tl = new TextLayout(text, g.getFont(), g.getFontRenderContext());
        tl.draw(g, padding + 0.6f, padding + fm.getAscent());
        g.dispose();
        
        img = Scalr.resize(img, Method.SPEED, img.getWidth() * 10, img.getHeight() * 10);

        ImageIO.write(img, "png", outFile);
    }

}
