package org.j3d;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Surefire;

import org.j3d.texture.procedural.TextureGenerator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Locale;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class TextureGeneratorTest extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(TextureGeneratorTest.class);

    private final TextureGenerator gen = new TextureGenerator();
    private final int textureWidth = 800;
    private final int textureHeight = 600;
    private final float[] texture = new float[textureWidth * textureHeight];
    float min, max;

    public TextureGeneratorTest() {
        super(TextureGeneratorTest.class.getSimpleName());

        setPreferredSize(new Dimension(800, 600));
        pack();
    }

    @Test
    public void test() {
        assumeTrue(Surefire.isSingleTestExecution());

        genAndShowTexture(4, 1f);
        genAndShowTexture(40, 1f);
        genAndShowTexture(400, 1f);
        genAndShowTexture(4, 5f);
        genAndShowTexture(40, 5f);
        genAndShowTexture(400, 5f);
    }

    private void normalizeTexture() {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (int i = 0; i < texture.length; i++) {
            float value = texture[i];
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }
        LOG.info("min = " + min);
        LOG.info("max = " + max);
        float maxDist = max - min;
        for (int i = 0; i < texture.length; i++) {
            float value = texture[i];
            value -= min;
            value /= maxDist;
            if (value < 0f) {
                LOG.info("" + value);
                value = 0f;
            }
            if (value > 1f) {
                LOG.info("" + value);
                value = 1f;
            }
            texture[i] = value;
        }
    }

    private void genAndShowTexture(int freq, float zScale) {
        resetTexture();
        gen.generateSynthesisTexture(texture, freq, zScale, textureWidth, textureHeight);
        normalizeTexture();
        setTitle(String.format(Locale.ROOT, "%s: freq=%d, zScale=%.3f", TextureGeneratorTest.class.getSimpleName(),
                freq, zScale));
        AWTUtils.showFrameAndWaitForCloseByUser(this);
    }

    private void resetTexture() {
        for (int i = 0; i < texture.length; i++) {
            texture[i] = 0f;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        Rectangle r = g.getClipBounds();
        for (int x = 0; x < r.width; x++) {
            for (int y = 0; y < r.height; y++) {
                float value = texture[y * textureWidth + x];
                g2.setColor(Color.getHSBColor(0f, 0f, value));
                g2.fillRect(r.x + x, r.y + y, 1, 1);
            }
        }
    }
}
