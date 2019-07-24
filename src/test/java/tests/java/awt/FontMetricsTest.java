package tests.java.awt;

import static org.junit.Assert.assertNotNull;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FontMetricsTest {

    private static final Logger LOG = LoggerFactory.getLogger(FontMetricsTest.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(FontMetricsTest.class);

    @Test
    public void test() throws IOException {
        BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        FontMetrics fm = g.getFontMetrics();
        assertNotNull(fm);
        LOG.info("" + g.getFont());
        LOG.info("" + g.getFontMetrics());
        LOG.info("" + fm.getStringBounds("aaa", g));
        LOG.info("" + fm.getStringBounds("aag", g));
        LOG.info("" + fm.getStringBounds("ala", g));
        LOG.info("" + fm.getStringBounds("alg", g));
        Rectangle2D r = fm.getStringBounds("alg", g);
        g.dispose();

        int padding = 1;
        img = new BufferedImage((int) Math.ceil(r.getWidth() + 2 * padding),
            (int) Math.ceil(r.getHeight() + 2 * padding),
            BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) img.getGraphics();
        g.setBackground(Color.black);
        g.setColor(Color.white);
        g.clearRect(0, 0, img.getWidth(), img.getHeight());
        g.drawString("alg", padding, padding + fm.getAscent());
        g.dispose();
        
        ImageIO.write(img, "png", new File(TEMP_DIR, "test.png"));
        //@insert:image:test.png@
    }
}
