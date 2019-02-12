package tests.java.awt.image;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.gfx.ImageUtils;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class BufferedImageTest extends InteractiveTestBase {

    private static final File TEMP_DIR = Maven.getTempTestDir(BufferedImageTest.class);
    private static final Logger LOG = LoggerFactory.getLogger(BufferedImageTest.class);
    private Random r;

    @Before
    public void before() {
        r = new Random(0);
    }

    @Test
    public void testDownsampling() throws InterruptedException, InvocationTargetException {
        openWindow();

        BufferedImage img = new BufferedImage(400, 200, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.WHITE);
        g.drawLine(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.GRAY);
        g.fillArc(0, img.getHeight() / 2, img.getWidth(), img.getHeight(), 0, 360);
        append(img, "input");

        BufferedImage img2 = downsample(img, null);
        append(img2, "downsampling, no interpolation");
        img2 = downsample(img, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        append(img2, "downsampling, NN");
        img2 = downsample(img, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        append(img2, "downsampling, bilinear");
        img2 = downsample(img, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        append(img2, "downsampling, bicubic");

        writeWindowAsPng(new File(TEMP_DIR, "BufferedImageTest.png"));
        // @insert:image:BufferedImageTest.png@
        waitForWindowClosing();
    }

    private BufferedImage downsample(BufferedImage input, Object interpolationHint) {
        BufferedImage img = ImageUtils.deepCopy(input);
        Graphics2D g = (Graphics2D) img.getGraphics();
        if (interpolationHint != null) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolationHint);
        }
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.drawImage(input, img.getWidth() / 12, img.getHeight() / 3, img.getWidth() * 5 / 12, img.getHeight() * 2 / 3,
            0, 0, input.getWidth(), input.getHeight(), null);
        ((Graphics2D) img.getGraphics()).drawImage(img,
            img.getWidth() * 6 / 12, img.getHeight() * 3 / 12, img.getWidth(), img.getHeight() * 9 / 12,
            img.getWidth() * 4 / 24, img.getHeight() * 10 / 24, img.getWidth() * 8 / 24, img.getHeight() * 14 / 24,
            null);
        return img;
    }

    @Test
    public void testTypeByteGray() {
        BufferedImage img = new BufferedImage(256, 1, BufferedImage.TYPE_BYTE_GRAY);
        Set<Color> effectiveColors = new HashSet<Color>();
        for (int i = 0; i < 256; i++) {
            Color shadeOfGray = new Color(i, i, i, 255);
            img.setRGB(i, 0, shadeOfGray.getRGB());
            int effectiveColor = img.getRGB(i, 0);
            effectiveColors.add(new Color(effectiveColor));
        }
        assertEquals(183, effectiveColors.size());
        for (int i = 0; i < 1024 * 1024; i++) {
            Color randomColor = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), 255);
            img.setRGB(0, 0, randomColor.getRGB());
            int effectiveColor = img.getRGB(0, 0);
            effectiveColors.add(new Color(effectiveColor));
        }
        assertEquals(183, effectiveColors.size());
        LOG.info("number of effective colors for color model: " + effectiveColors.size()
            + " [" + img.getColorModel() + "]");
    }

    @Test
    public void testTypeByteIndexed() {
        BufferedImage img = new BufferedImage(256, 1, BufferedImage.TYPE_BYTE_INDEXED);
        Set<Color> effectiveColors = new HashSet<Color>();
        for (int i = 0; i < 256; i++) {
            Color shadeOfGray = new Color(i, i, i, 255);
            img.setRGB(i, 0, shadeOfGray.getRGB());
            int effectiveColor = img.getRGB(i, 0);
            LOG.info("color distance = " + ImageUtils.colorDist(effectiveColor, shadeOfGray.getRGB()));
            effectiveColors.add(new Color(effectiveColor));
        }
        LOG.info("max color distance: " + ImageUtils.colorDist(Color.WHITE.getRGB(), Color.BLACK.getRGB()));
        assertEquals(44, effectiveColors.size());
        for (int i = 0; i < 1024 * 1024; i++) {
            Color randomColor = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), 255);
            img.setRGB(0, 0, randomColor.getRGB());
            int effectiveColor = img.getRGB(0, 0);
            effectiveColors.add(new Color(effectiveColor));
        }
        assertEquals(254, effectiveColors.size());
        assertFalse(img.getColorModel().hasAlpha());
        LOG.info("number of effective colors for color model: " + effectiveColors.size()
            + " [" + img.getColorModel() + "]");
    }

    @Test
    public void testTypeByteIndexed2() {
        byte[] icmValues = new byte[256];
        for (int i = 0; i < 256; i++) {
            icmValues[i] = (byte) i;
        }
        IndexColorModel icm = new IndexColorModel(8, 256, icmValues, icmValues, icmValues);
        BufferedImage img = new BufferedImage(256, 1, BufferedImage.TYPE_BYTE_INDEXED, icm);
        Set<Color> effectiveColors = new HashSet<Color>();
        for (int i = 0; i < 256; i++) {
            Color shadeOfGray = new Color(i, i, i, 255);
            img.setRGB(i, 0, shadeOfGray.getRGB());
            int effectiveColor = img.getRGB(i, 0);
            LOG.info("color distance = " + ImageUtils.colorDist(effectiveColor, shadeOfGray.getRGB()));
            effectiveColors.add(new Color(effectiveColor));
        }
        LOG.info("max color distance: " + ImageUtils.colorDist(Color.WHITE.getRGB(), Color.BLACK.getRGB()));
        assertEquals(256, effectiveColors.size());
        assertFalse(img.getColorModel().hasAlpha());
        LOG.info("number of effective colors for color model: " + effectiveColors.size()
            + " [" + img.getColorModel() + "]");
    }

    @Test
    public void testIndexColorModel() {
        byte[] icmValues = new byte[256];
        for (int i = 0; i < 256; i++) {
            icmValues[i] = (byte) i;
        }
        IndexColorModel icm = new IndexColorModel(8, 256, icmValues, icmValues, icmValues);
        BufferedImage img = new BufferedImage(256, 1, BufferedImage.TYPE_BYTE_INDEXED, icm);

        assertTrue(img.getColorModel() instanceof IndexColorModel);

        icm = (IndexColorModel) img.getColorModel();
        assertEquals(icmValues.length, icm.getMapSize());
        assertArrayEquals(new int[] { 8, 8, 8 }, icm.getComponentSize());

        byte[] testValues = new byte[icmValues.length];
        icm.getReds(testValues);
        assertArrayEquals(icmValues, testValues);
        icm.getGreens(testValues);
        assertArrayEquals(icmValues, testValues);
        icm.getBlues(testValues);
        assertArrayEquals(icmValues, testValues);
    }

}
