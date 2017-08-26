package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.env.IDE;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.gfx.AnimatedGifOutputStream;
import com.github.jjYBdx4IL.utils.gfx.ImageUtils;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import com.github.jjYBdx4IL.utils.junit4.RDRunner;
import com.github.jjYBdx4IL.utils.klass.ClassReloader;

@RunWith(RDRunner.class)
public class SpinnerTest extends InteractiveTestBase implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SpinnerTest.class);
    private final static File tempDir = Maven.getTempTestDir(SpinnerTest.class);

    final Dimension renderSize = new Dimension(256, 256);
    final Dimension gifSize = new Dimension(24, 24);

    final int w = renderSize.width;
    final int h = renderSize.height;
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) img.getGraphics();

    final int timeBetweenFramesMillis = 40;
    final double durationMillis = 3000.;
    final int nFrames = 1 + (int)(durationMillis / timeBetweenFramesMillis);

    @BeforeClass
    public static void beforeClass() throws IOException {
        FileUtils.cleanDirectory(tempDir);
    }

    @Before
    public void before() {
        openWindow(true);
        waitForSwing(); 
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON); 
    }

    /**
     *
     * @param g
     * @param w
     * @param h
     * @param n number of segments
     * @param f fraction of angular extent covered
     */
    public static void drawSlicedArc(
            Graphics2D g, double w, double h, int n, double start, double f) {
        double extent = 360. * f / n;
        for (int i = 0; i < n; i++) {
            drawSlicedArcSegment(g, w, h, 360. * i / n + 90. + start, extent);
        }
    }

    public static void drawSlicedArcSegment(Graphics2D g, double w, double h, double start, double extent) {
        double padding = w / 10.;
        Arc2D arc = new Arc2D.Double(padding, padding, w - 2 * padding, h - 2 * padding, start, extent, Arc2D.PIE);
        g.fill(arc);
        padding += w / 10.;
        arc = new Arc2D.Double(padding, padding, w - 2 * padding, h - 2 * padding, start - extent / 2., 360., Arc2D.PIE);
        Color c = g.getColor();
        g.setColor(g.getBackground());
        g.fill(arc);
        g.setColor(c);
    }

    protected void produceAnimatedGif(File output, Dimension gifSize, int nFrames, double durationMillis, RunnableImageProducer r) throws IOException, InvocationTargetException, InterruptedException {
        int timeBetweenFramesMillis = (int) (durationMillis / nFrames);
        try (AnimatedGifOutputStream animGifOs = new AnimatedGifOutputStream(output, timeBetweenFramesMillis)) {
            byte[] icmValues = new byte[256];
            for (int i=0; i<256; i++) {
                icmValues[i] = (byte)i;
            }
            IndexColorModel icm = new IndexColorModel(8, 256, icmValues, icmValues, icmValues);
            BufferedImage img = new BufferedImage(gifSize.width, gifSize.height, BufferedImage.TYPE_BYTE_INDEXED, icm);
            Graphics2D g = (Graphics2D) img.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            BufferedImage displayCopy = null;
            long realtime = IDE.runningInsideIDE() ? System.currentTimeMillis() : -1;
            for (int i = 0; i < nFrames; i++) {
                final BufferedImage out = r.run(i, nFrames, i / (nFrames - 1.));
                g.drawImage(out, 0, 0, img.getWidth(), img.getHeight(), null);
                ImageIO.write(img, "gif", new File(output.getPath()+"."+i+".gif"));
                animGifOs.append(img); 

                if (displayCopy == null) {
                    displayCopy = ImageUtils.deepCopy(out);
                    append(displayCopy, true);
                }

                final BufferedImage displayCopyRef = displayCopy;
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        Graphics2D g = (Graphics2D) displayCopyRef.getGraphics();
                        g.drawImage(out, 0, 0, displayCopyRef.getWidth(), displayCopyRef.getHeight(), null);
                        g.dispose();
                        jf.repaint(); 
                    }  
                }); 

                long lag = System.currentTimeMillis() - realtime;
                if (lag < 0) {
                    Thread.sleep(-lag);
                }

                realtime += realtime == -1 ? 0 : timeBetweenFramesMillis;
            }
        }
    }

    @Test
    public void testSpinner1() throws InterruptedException, InvocationTargetException, FontFormatException, IOException {
        //before();

        final Color bkgd = Color.BLACK;
        final int nSegments = 5;

        produceAnimatedGif(new File(tempDir, "animated1.gif"), gifSize, nFrames/4, durationMillis/4, new RunnableImageProducer() {

            @Override
            public BufferedImage run(int frameIndex, int nFrames, double timeFraction) {

                g.setColor(bkgd);
                g.fillRect(0, 0, img.getWidth(), img.getHeight());
                g.setColor(Color.GRAY);
                drawSlicedArc(g, w, h, nSegments, timeFraction * 360./nSegments, .5);
                return img;
            }
        });
    }

    @Test
    public void testSpinner4() throws InterruptedException, InvocationTargetException, FontFormatException, IOException {
//        before();

        final Color bkgd = Color.BLACK;

        produceAnimatedGif(new File(tempDir, "animated4.gif"), gifSize, nFrames, durationMillis, new RunnableImageProducer() {

            @Override
            public BufferedImage run(int frameIndex, int nFrames, double timeFraction) {

                g.setColor(bkgd);
                g.fillRect(0, 0, img.getWidth(), img.getHeight());
                g.setColor(Color.GRAY);
                drawSlicedArc(g, w, h, frameIndex+1, 0., .5);
                return img;
            }
        });
    }

    @Test
    public void testSpinner3() throws InterruptedException, InvocationTargetException, FontFormatException, IOException {
//        before();

        final Color bkgd = Color.BLACK;
        final int nSegments = 5;

        produceAnimatedGif(new File(tempDir, "animated3.gif"), gifSize, nFrames, durationMillis, new RunnableImageProducer() {

            @Override
            public BufferedImage run(int frameIndex, int nFrames, double timeFraction) {

                double extentFraction = timeFraction;

                g.setColor(bkgd);
                g.fillRect(0, 0, img.getWidth(), img.getHeight());
                g.setColor(Color.GRAY);
                if (timeFraction <= .5) {
                    drawSlicedArc(g, w, h, nSegments, 0., extentFraction * 2.);
                } else {
                    drawSlicedArc(g, w, h, 1, 0., 1.);
                    g.setColor(bkgd);
                    drawSlicedArc(g, w, h, nSegments, 0., (extentFraction-.5) * 2.);
                }
                return img;
            }
        });
    }

    @Test
    public void testSpinner2() throws InterruptedException, InvocationTargetException, FontFormatException, IOException {
//        before();

        final Color bkgd = Color.BLACK;

        produceAnimatedGif(new File(tempDir, "animated2.gif"), gifSize, nFrames, durationMillis, new RunnableImageProducer() {

            @Override
            public BufferedImage run(int frameIndex, int nFrames, double timeFraction) {

                double extentFraction = timeFraction;

                g.setColor(bkgd);
                g.fillRect(0, 0, img.getWidth(), img.getHeight());
                g.setColor(Color.GRAY);
                if (timeFraction < .5) {
                    drawSlicedArc(g, w, h, 5, 0., extentFraction * 2.);
                } else {
                    drawSlicedArc(g, w, h, 5, 0., (1. - extentFraction) * 2.);
                }
                return img;
            }
        });
    }

    @Override
    public void run() {
        try {
            testSpinner1();
        } catch (Throwable ex) {
            LOG.error("", ex);
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        LOG.info("started");
        beforeClass();
        Thread t = ClassReloader.watchLoadAndRun("target/test-classes", SpinnerTest.class.getName());
        t.join();
    }

    public interface RunnableImageProducer {

        public BufferedImage run(int frameIndex, int nFrames, double timeFraction);
    }
}
