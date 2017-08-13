package org.j3d;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;

import org.j3d.texture.procedural.TextureGenerator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class TextureGeneratorTest extends JFrame implements ChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(TextureGeneratorTest.class);

    private final TextureGenerator gen = new TextureGenerator();
    private final int textureWidth = 800;
    private final int textureHeight = 600;
    private final float[] texture = new float[textureWidth * textureHeight];
    float min, max;
    private final JPanel paintPanel = new JPanel() {
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
    };
    private final JLabel freqLabel = new JLabel();
    private final JSlider freqSlider = new JSlider(2, 1000, 4);
    private final JLabel zScaleLabel = new JLabel();
    private final JSlider zScaleSlider = new JSlider(1, 1000, 1000);

    @Test
    public void test() {
        regenTexture();
        AWTUtils.showFrameAndWaitForCloseByUserTest(this);
    }

    public TextureGeneratorTest() {
        super(TextureGeneratorTest.class.getSimpleName());

        Container container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        container.add(paintPanel, BorderLayout.CENTER);
        container.add(freqLabel, BorderLayout.CENTER);
        container.add(freqSlider, BorderLayout.CENTER);
        container.add(zScaleLabel, BorderLayout.CENTER);
        container.add(zScaleSlider, BorderLayout.CENTER);

        freqSlider.addChangeListener(this);
        zScaleSlider.addChangeListener(this);

        paintPanel.setPreferredSize(new Dimension(800, 600));
        setTitle(TextureGeneratorTest.class.getSimpleName());

        pack();
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

    private void regenTexture() {
        resetTexture();
        int freq = freqSlider.getValue();
        float zScale = (float) zScaleSlider.getValue() / zScaleSlider.getMaximum();
        freqLabel.setText(String.format(Locale.ROOT, "freq = %d:", freq));
        zScaleLabel.setText(String.format(Locale.ROOT, "zScale = %.3f:", zScale));
        gen.generateSynthesisTexture(texture, freq, zScale, textureWidth, textureHeight);
        normalizeTexture();
    }

    private void resetTexture() {
        for (int i = 0; i < texture.length; i++) {
            texture[i] = 0f;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        regenTexture();
        paintPanel.repaint();
    }

}
