package org.j3d;

import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;
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
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class TextureGeneratorTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(TextureGeneratorTest.class);
    private static final Logger LOG = LoggerFactory.getLogger(TextureGeneratorTest.class);

    @Test
    public void test() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        TestFrame frame = new TestFrame();
        frame.regenTexture();
        AWTUtils.showFrameAndWaitForCloseByUserTest(frame, new File(TEMP_DIR, "test.png"));
        // @insert:image:test.png@
    }

    public static class TestFrame extends JFrame implements ChangeListener {

        private final TextureGenerator gen = new TextureGenerator();
        private final int textureWidth = 800;
        private final int textureHeight = 600;
        private final float[] texture = new float[textureWidth * textureHeight];
        float min, max;

        public final JPanel paintPanel = new JPanel() {
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
        public final JLabel freqLabel = new JLabel();
        public final JSlider freqSlider = new JSlider(2, 1000, 4);
        public final JLabel zScaleLabel = new JLabel();
        public final JSlider zScaleSlider = new JSlider(1, 1000, 1000);
        
        public TestFrame() {
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

        @Override
        public void stateChanged(ChangeEvent e) {
            regenTexture();
            paintPanel.repaint();
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

        public void regenTexture() {
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
    }

}
