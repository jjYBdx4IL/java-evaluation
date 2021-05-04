package tests.java.awt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;

import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import com.jhlabs.image.ImageUtils;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class RenderingHintsTest extends InteractiveTestBase {

    private static final File TEMP_DIR = Maven.getTempTestDir(RenderingHintsTest.class);
    private final Font font = new Font(Font.MONOSPACED, Font.BOLD, 30);
    private final RenderingHints hintsNone = new RenderingHints(null);
    private final RenderingHints hintsAA = new RenderingHints(null);
    private final RenderingHints hintsBicubic = new RenderingHints(null);

    @Before
    public void before() {
        hintsAA.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hintsBicubic.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    @Test
    public void test1() throws InterruptedException, InvocationTargetException {
        openWindow();
        BufferedImage img;

        img = getImage();
        drawString(img, hintsNone);
        magnify(img, hintsNone);
        append(img, "no rendering hints");

        img = getImage();
        drawString(img, hintsAA);
        magnify(img, hintsNone);
        append(img, "text with antialiasing");

        img = getImage();
        drawString(img, hintsBicubic);
        magnify(img, hintsNone);
        append(img, "text with bicubic interpolation");

        img = getImage();
        drawString(img, hintsNone);
        magnify(img, hintsBicubic);
        append(img, "zoom with bicubic interpolation");

        img = getImage();
        drawString(img, hintsAA);
        magnify(img, hintsBicubic);
        append(img, "text AA & zoom bicubic");

        writeWindowAsPng(new File(TEMP_DIR, "RenderingHintsTest.png"));
        // @insert:image:RenderingHintsTest.png@
        waitForWindowClosing();
    }

    private void magnify(BufferedImage img, RenderingHints hints) {
        Graphics2D g = (Graphics2D) img.getGraphics();
        final int h = img.getHeight();
        final int w = img.getWidth();
        g.setRenderingHints(hints);
        g.drawImage(img,
            w * 10 / 100, h * 10 / 100, w * 90 / 100, h * 30 / 100,
            w * 10 / 100, h * 45 / 100, w * 30 / 100, h * 55 / 100,
            null);
    }

    private BufferedImage getImage() {
        int w = 400;
        int h = w * 9 / 16;
        BufferedImage img = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage();
        img = ImageUtils.getSubimage(img, 0, 0, w, h);
        return img;
    }

    private void drawString(BufferedImage img, RenderingHints hints) {
        String text = "SOME test text...";
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.addRenderingHints(hints);
        g.setColor(Color.WHITE);
        g.setFont(font);
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);
        g.drawString(text,
            (int) (img.getWidth() - bounds.getWidth()) / 2,
            (int) Math.nextUp(-bounds.getMinY()) + (int) (img.getHeight() - bounds.getHeight()) / 2);
        g.setRenderingHints(hintsNone);
    }

}
