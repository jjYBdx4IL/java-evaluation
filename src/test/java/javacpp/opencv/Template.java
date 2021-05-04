package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_core.CV_MAKETYPE;
import static org.bytedeco.opencv.global.opencv_core.NORM_MINMAX;
import static org.bytedeco.opencv.global.opencv_core.normalize;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2BGR;
import static org.bytedeco.opencv.global.opencv_imgproc.Canny;
import static org.bytedeco.opencv.global.opencv_imgproc.THRESH_BINARY;
import static org.bytedeco.opencv.global.opencv_imgproc.blur;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.threshold;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.remoterobot.RobotClient;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Template extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(Template.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(Template.class);
    private static final boolean TRACE_CAPTURES = true;

    private Mat mat = null;
    private final Mat origMat;
    private final String desc;
    private final float minMatchValue;
    private boolean isCvtToGray = true;
    private boolean isBlur = false;
    // normalization might not be a good idea because the result depends on the
    // non-matching surroundings (normalize the best match instead and then use
    // the match value against that?)
    private boolean isNormalize = false;
    private boolean isDetectEdges = false;
    private boolean isThreshold = false;
    private int thresholdLevel = 240;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS");
    private Match lastMatch = null;
    private int lastMatchCount = -1;

    public Template(String tplResourceId, float minMatchValue, RobotClient bot) throws IOException {
        super();
        this.minMatchValue = minMatchValue;
        this.bot = bot;
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.origMat = getResAsMat(tplResourceId);
        this.desc = tplResourceId;
    }

    public Template(String desc, BufferedImage tpl, float minMatchValue, RobotClient bot) throws IOException {
        super();
        this.minMatchValue = minMatchValue;
        this.bot = bot;
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.origMat = toMat(tpl);
        this.desc = desc;
    }

    public Template(String desc, Mat tpl, float minMatchValue, RobotClient bot) throws IOException {
        super();
        this.minMatchValue = minMatchValue;
        this.bot = bot;
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.origMat = new Mat(tpl);
        this.desc = desc;
    }

    public Match findBestMatch() throws IOException {
        return findBestMatch(null);
    }

    public Match findBestMatchRetry(Rectangle where, int tries, long retryDelayMs) throws IOException {
        for (int i = 0; i < tries; i++) {
            if (i > 0) {
                sleep(retryDelayMs);
            }
            Match m = findBestMatch(where);
            if (m != null) {
                return m;
            }
        }
        return null;
    }

    public Match findBestMatch(Rectangle where) throws IOException {
        if (where == null) {
            where = new Rectangle(bot.getScreenSize());
        }
        correctSsRegion(where);
        Mat screenshot = createScreenCapture(where);
        if (desc != null) {
            LOG.info(desc);
        }
        return findBestMatch(where, screenshot);
    }

    public Match findBestMatch(Rectangle where, Mat screenshot) throws IOException {
        assertNotNull(where);
        log(screenshot);
        getMat();
        screenshot = applyParamsToMat(screenshot);
        org.bytedeco.opencv.opencv_core.Point p = findBestMatch(screenshot, mat, minMatchValue);
        lastMatch = null;
        if (p != null) {
            lastMatch = new Match(where, mat, bot, screenshot, new Point(p.x(), p.y()), lastBestMatchValue);
        }
        return lastMatch;
    }

    public int countMatches(Rectangle where) throws IOException {
        getMat();
        correctSsRegion(where);
        Mat screenshot = createScreenCapture(where);
        screenshot = applyParamsToMat(screenshot);
        if (desc != null) {
            LOG.info(desc);
        }
        lastMatchCount = countMatches(screenshot, mat, minMatchValue, Math.min(mat.cols(), mat.rows()));
        return lastMatchCount;
    }

    public int requireMatches(Rectangle where, int requiredCount, int tries, long retryDelayMs) throws IOException {
        if (tries < 1) {
            throw new IllegalArgumentException();
        }
        if (requiredCount < 0) {
            throw new IllegalArgumentException();
        }
        getMat();
        correctSsRegion(where);
        if (desc != null) {
            LOG.info(desc);
        }
        for (int i = 0; i < tries; i++) {
            Mat screenshot = createScreenCapture(where);
            screenshot = applyParamsToMat(screenshot);
            lastMatchCount = countMatches(screenshot, mat, minMatchValue, Math.min(mat.cols(), mat.rows()));
            if (lastMatchCount == requiredCount) {
                break;
            }
            sleep(retryDelayMs);
        }
        return lastMatchCount;
    }

    private void sleep(long retryDelayMs) {
        try {
            Thread.sleep(Math.max(1, retryDelayMs));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Mat applyParamsToMat(Mat m) {
        log(m);
        if (isCvtToGray) {
            m = toGray(m);
            log(m);
        }
        if (isNormalize) {
            Mat normalized = new Mat(m.size(), m.type());
            normalize(m, normalized, 0, 255, NORM_MINMAX, m.type(), null);
            m = normalized;
            log(m);
        }
        if (isThreshold) {
            Mat thresholded = new Mat(m.size(), m.type());
            threshold(m, thresholded, thresholdLevel, 255, THRESH_BINARY);
            m = thresholded;
            log(m);
        }
        if (isBlur) {
            Mat blurred = new Mat(m.size(), m.type());
            blur(m, blurred, new Size(3, 3));
            m = blurred;
            log(m);
        }
        if (isDetectEdges) {
            Mat edges = new Mat(m.size(), m.type());
            Canny(m, edges, 10, 100);
            m = edges;
            log(m);
        }
        return m;
    }

    public Mat getMat() throws IOException {
        if (mat == null) {
            mat = applyParamsToMat(origMat);
        }
        return mat;
    }

    public boolean isCvtToGray() {
        return isCvtToGray;
    }

    public void setCvtToGray(boolean isCvtToGray) {
        mat = null;
        this.isCvtToGray = isCvtToGray;
    }

    public boolean isBlur() {
        return isBlur;
    }

    public void setBlur(boolean isBlur) {
        mat = null;
        this.isBlur = isBlur;
    }

    public boolean isNormalize() {
        return isNormalize;
    }

    public void setNormalize(boolean isNormalize) {
        mat = null;
        this.isNormalize = isNormalize;
    }

    public boolean isDetectEdges() {
        return isDetectEdges;
    }

    public void setDetectEdges(boolean isDetectEdges) {
        mat = null;
        this.isDetectEdges = isDetectEdges;
    }

    public boolean isThreshold() {
        return isThreshold;
    }

    public void setThreshold(boolean isThreshold) {
        mat = null;
        this.isThreshold = isThreshold;
    }

    public int getThresholdLevel() {
        return thresholdLevel;
    }

    /**
     * Primarily useful when using grayscale and normalization. Then values span
     * the range 0..255 and you should set a value in between those two limits.
     * 
     * @param thresholdLevel
     *            the thresholdLevel, default 250
     */
    public void setThresholdLevel(int thresholdLevel) {
        this.thresholdLevel = thresholdLevel;
    }

    private void log(Mat m) {
        if (!TRACE_CAPTURES) {
            return;
        }
        imwrite(new File(TEMP_DIR, sdf.format(new Date()) + ".png").getAbsolutePath(), m);
    }

    public Mat createScreenCapture(Rectangle where) throws IOException {
        correctSsRegion(where);
        BufferedImage image = bot.createScreenCapture(where);
        if (image.getType() != BufferedImage.TYPE_INT_RGB) {
            throw new RuntimeException("unexpected image format: " + image.getType());
        }

        DataBuffer db = image.getRaster().getDataBuffer();
        assertEquals(db.getDataType(), DataBuffer.TYPE_INT);
        int[] data = ((DataBufferInt) db).getData();

        Frame f = new Frame(image.getWidth(), image.getHeight(), Frame.DEPTH_UBYTE, 4);
        long step = f.imageStride * Math.abs(f.imageDepth) / 8;
        int type = CV_MAKETYPE(f.imageDepth, f.imageChannels);
        Mat m = new Mat(f.imageHeight, f.imageWidth, type, new IntPointer(data), step);
        cvtColor(m, m, COLOR_BGRA2BGR);
        return m;
    }

    public Match getLastMatch() {
        return lastMatch;
    }

    public int getLastMatchCount() {
        return lastMatchCount;
    }

    public boolean isAbsent(Rectangle where) throws IOException {
        findBestMatch(where);
        return lastBestMatchValue < minMatchValue * .9f;
    }

    // warning! for some reason the width (and maybe height) of the screen
    // capture regions must be a multiple of 2.
    private void correctSsRegion(Rectangle region) {
        if (region.width % 2 == 1) {
            region.width++;
        }
        if (region.height % 2 == 1) {
            region.height++;
        }
    }
    
    public String getDesc() {
        return desc;
    }

}
