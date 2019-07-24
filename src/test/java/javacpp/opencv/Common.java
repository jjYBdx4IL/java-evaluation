package javacpp.opencv;

import static org.bytedeco.opencv.global.opencv_core.CV_32FC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.CV_MAKETYPE;
import static org.bytedeco.opencv.global.opencv_core.minMaxLoc;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2BGR;
import static org.bytedeco.opencv.global.opencv_imgproc.TM_CCORR_NORMED;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.matchTemplate;
import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.utils.remoterobot.KeepAlive;
import com.github.jjYBdx4IL.utils.remoterobot.MouseButton;
import com.github.jjYBdx4IL.utils.remoterobot.RobotClient;
import com.github.jjYBdx4IL.utils.remoterobot.RobotServer;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Common {

    private static final Logger LOG = LoggerFactory.getLogger(Common.class);

    public static final java.awt.Point FIRST_WAYPOINT_POS = new java.awt.Point(80, 202);
    public static final java.awt.Point FIRST_WAYPOINT_2NDPOS = new java.awt.Point(80, 217);

    public static final long DEFAULT_DELAY_MS = 1000;
    protected float lastBestMatchValue = -1;

    protected Dimension screenDim;
    protected Point disableCtxMenuClickLoc;
    protected Rectangle inventoryRegion;
    protected Rectangle undockRegion;

    static {
        LoadOrderDetectorTest.loadLibs("jniopencv_imgproc", "jniopencv_imgcodecs", "jniopencv_core");
    }

    protected RobotClient bot = null;
    protected KeepAlive keepAlive = null;

    public Common() {
    }

    public void enableBot() throws IOException, AWTException {
        try {
            bot = RobotClient.connect(InetAddress.getLoopbackAddress(), RobotServer.DEFAULT_PORT);
        } catch (IOException ex) {
            LOG.info("reverting to local operation");
            bot = RobotClient.connect(null, RobotServer.DEFAULT_PORT);
        }
        bot.setAutoDelayMs(DEFAULT_DELAY_MS);
        bot.setSendInputDelayMs(DEFAULT_DELAY_MS);
        screenDim = bot.getScreenSize();
        disableCtxMenuClickLoc = new Point(3, screenDim.height * 9 / 10);
        inventoryRegion = new Rectangle(screenDim.width / 2, screenDim.height / 2,
            screenDim.width / 2, screenDim.height / 2);
        undockRegion = new Rectangle(screenDim.width * 2 / 3, 0, screenDim.width / 3,
            screenDim.height / 2);
        keepAlive = new KeepAlive();
        keepAlive.start();
    }

    public Point findBestMatch(Mat shot, Mat tpl, float minMatchValue) {
        int sizeX = shot.cols() - tpl.cols() + 1;
        int sizeY = shot.rows() - tpl.rows() + 1;
        if (sizeX < 1 || sizeY < 1) {
            return null;
        }
        Size size = new Size(sizeX, sizeY);
        Mat result = new Mat(size, CV_32FC1);
        matchTemplate(shot, tpl, result, TM_CCORR_NORMED);

        DoublePointer minVal = new DoublePointer();
        DoublePointer maxVal = new DoublePointer();
        Point min = new Point();
        Point max = new Point();
        minMaxLoc(result, minVal, maxVal, min, max, null);
        FloatBuffer fb = result.createBuffer();
        lastBestMatchValue = fb.get(max.x() + result.cols() * max.y());
        LOG.info("" + max.x() + " " + max.y() + " " + lastBestMatchValue);
        if (lastBestMatchValue >= minMatchValue) {
            return max;
        }
        return null;
    }

    public static int countMatches(Mat shot, Mat tpl, float minMatchValue, float minDistance) {
        int sizeX = shot.cols() - tpl.cols() + 1;
        int sizeY = shot.rows() - tpl.rows() + 1;
        if (sizeX < 1 || sizeY < 1) {
            return 0;
        }
        Size size = new Size(sizeX, sizeY);
        LOG.info("result size: " + size.width() + " " + size.height());
        Mat result = new Mat(size, CV_32FC1);
        matchTemplate(shot, tpl, result, TM_CCORR_NORMED);

        FloatBuffer fb = result.createBuffer();

        int pos = 0;
        List<Pt> matches = new ArrayList<>();
        while (fb.hasRemaining()) {
            float val = fb.get();
            Pt pt = new Pt(pos % result.cols(), pos / result.cols(), val);
            if (val >= minMatchValue) {
                if (minDist(pt, matches) >= minDistance) {
                    matches.add(pt);
                    LOG.info("added " + pt);
                } else {
                    LOG.info("skipped " + pt);

                }
            }
            pos++;
        }
        LOG.info("matches = " + matches.size());
        return matches.size();
    }

    public static void saveNormalized(String filename, Mat mat) {
        BufferedImage bi = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_BYTE_GRAY);

        float[] data = new float[mat.cols() * mat.rows()];
        FloatBuffer fb = mat.createBuffer();
        int pos = 0;
        while (fb.hasRemaining()) {
            data[pos] = fb.get();
            pos++;
        }
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (int i = 0; i < data.length; i++) {
            float val = data[i];
            min = Math.min(val, min);
            max = Math.max(val, max);
        }
        float mult = 255.999f / (max - min);
        LOG.info("min = " + min + ", max = " + max);

        WritableRaster r = bi.getRaster();
        int[] pixels = new int[mat.cols() * mat.rows()];
        for (int i = 0; i < data.length; i++) {
            pixels[i] = (int) ((data[i] - min) * mult);
        }
        r.setPixels(0, 0, r.getWidth(), r.getHeight(), pixels);
        try {
            File out = new File(filename + ".png");
            ImageIO.write(bi, "png", out);
            LOG.info("written: " + out.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static float minDist(Pt pt, List<Pt> matches) {
        float minDist = Float.MAX_VALUE;
        for (Pt _pt : matches) {
            minDist = Math.min(minDist, _pt.distance(pt));
        }
        return minDist;
    }

    public static class Pt {
        public int x, y;
        public float val;

        public Pt(int x, int y, float val) {
            this.x = x;
            this.y = y;
            this.val = val;
        }

        public float distance(Pt other) {
            return (float) Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
        }

        @Override
        public String toString() {
            return "Pt [x=" + x + ", y=" + y + ", val=" + val + "]";
        }
    }

    public final void click(Point p) throws AWTException, InterruptedException, IOException {
        this.click(p.x(), p.y());
    }

    public final void click(java.awt.Point p) throws AWTException, InterruptedException, IOException {
        this.click(p.x, p.y);
    }

    public final void click(int x, int y) throws AWTException, InterruptedException, IOException {
        LOG.info("click at " + x + ", " + y);
        bot.mouseMove(x, y);
        click();
    }

    public final void click() throws AWTException, InterruptedException, IOException {
        bot.mousePress(MouseButton.LEFT);
        bot.mouseRelease(MouseButton.LEFT);
    }

    public final void rightclick(Point p) throws AWTException, InterruptedException, IOException {
        rightclick(p.x(), p.y());
    }

    public final void rightclick(java.awt.Point p) throws AWTException, InterruptedException, IOException {
        rightclick(p.x, p.y);
    }

    public final void rightclick(int x, int y) throws AWTException, InterruptedException, IOException {
        bot.mouseMove(x, y);
        bot.mousePress(MouseButton.RIGHT);
        bot.mouseRelease(MouseButton.RIGHT);
    }

    public final void keypress(char c) throws AWTException, InterruptedException, IOException {
        bot.keyPress((int) c);
        bot.keyRelease((int) c);
    }

    public final void keypress(int code) throws AWTException, InterruptedException, IOException {
        bot.keyPress(code);
        bot.keyRelease(code);
    }

    public final void keypress(int mod, int code) throws AWTException, InterruptedException, IOException {
        bot.keyPress(mod);
        bot.keyPress(code);
        bot.keyRelease(code);
        bot.keyRelease(mod);
    }

    public Mat getResAsMat(String tplResourceId) throws IOException {
        byte[] data;
        try (InputStream is = getClass().getResourceAsStream(tplResourceId)) {
            data = IOUtils.toByteArray(is);
        }
        // this copies the encoded data to native memory:
        Mat raw = new Mat(data);
        Mat m = imdecode(raw, IMREAD_COLOR);
        return m;
    }

    public static Mat toGray(Mat color) {
        Mat grey = new Mat(color.size(), CV_8UC1);
        cvtColor(color, grey, COLOR_BGR2GRAY);
        return grey;
    }

    public static Mat toMat(BufferedImage bi) {
        if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
            throw new IllegalArgumentException("only RGB image type allowed");
        }

        DataBuffer db = bi.getRaster().getDataBuffer();
        assertEquals(db.getDataType(), DataBuffer.TYPE_INT);
        int[] data = ((DataBufferInt) db).getData();

        Frame f = new Frame(bi.getWidth(), bi.getHeight(), Frame.DEPTH_UBYTE, 4);
        long step = f.imageStride * Math.abs(f.imageDepth) / 8;
        int type = CV_MAKETYPE(f.imageDepth, f.imageChannels);
        Mat m = new Mat(f.imageHeight, f.imageWidth, type, new IntPointer(data), step);

        // make opaque
        cvtColor(m, m, COLOR_BGRA2BGR);

        return m;
    }

    public static BufferedImage toBufferedImage(Mat mat) {
        Java2DFrameConverter jcv = new Java2DFrameConverter();
        return jcv.convert(new OpenCVFrameConverter.ToIplImage().convert(mat));
    }

    public static Mat getExampleMat() {
        BufferedImage bi = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage();

        OpenCVFrameConverter.ToIplImage cv = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter jcv = new Java2DFrameConverter();
        return cv.convertToMat(jcv.convert(bi));
    }

    public static Mat copyRegion(Mat input, int x, int y, int width, int height) {
        Mat cropped = new Mat(new Size(width, height), input.type());
        int dtop = y;
        int dbottom = input.rows() - dtop - cropped.rows();
        int dleft = x;
        int dright = input.cols() - dleft - cropped.cols();
        input.adjustROI(-dtop, -dbottom, -dleft, -dright).copyTo(cropped);
        return cropped;
    }

    public boolean isShuttingDown() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        boolean b = c.get(Calendar.HOUR_OF_DAY) == 10 && c.get(Calendar.MINUTE) >= 50;
        if (b) {
            LOG.warn("server shutdown imminent");
        }
        return b;
    }

    public boolean activateContextMenuEntry(Template atTpl, Rectangle where, Template... contextEntryTpl)
        throws IOException, AWTException, InterruptedException {

        Match match = atTpl.findBestMatchRetry(where, 5, 1000);
        return activateContextMenuEntry(match.getAbsoluteMatchCenterPos(), contextEntryTpl);
    }

    public boolean activateContextMenuEntry(java.awt.Point at, Template... contextEntryTpl)
        throws IOException, AWTException, InterruptedException {
        return activateContextMenuEntry(at, 1, true, contextEntryTpl);
    }

    public boolean activateContextMenuEntry(java.awt.Point at, int tries, boolean clickFirst,
        Template... contextEntryTpls)
        throws IOException, AWTException, InterruptedException {

        if (contextEntryTpls.length < 1) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < tries; i++) {
            if (i > 0) {
                resetMouse();
            }

            if (clickFirst) {
                // undo multi-selection
                click(at);
            }
            rightclick(at);

            Rectangle region = new Rectangle(at.x, at.y - 50, 300, 500);
            region.x += Math.min(0, screenDim.width - region.x - region.width);
            region.y += Math.min(0, screenDim.height - region.y - region.height);

            Mat screenshot = contextEntryTpls[0].createScreenCapture(region);

            for (Template tpl : contextEntryTpls) {
                Match match = tpl.findBestMatch(region, screenshot);
                if (match != null) {
                    match.clickMatchCenter();
                    return true;
                }
            }

        }

        if (tries == 1) {
            resetMouse();
        }
        return false;
    }

    protected void resetMouse() throws AWTException, InterruptedException, IOException {
        rightclick(disableCtxMenuClickLoc);
        click(disableCtxMenuClickLoc);
    }
}
