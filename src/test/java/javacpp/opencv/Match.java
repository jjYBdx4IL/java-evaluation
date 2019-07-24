package javacpp.opencv;

import com.github.jjYBdx4IL.utils.remoterobot.RobotClient;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.opencv_core.Mat;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

public class Match extends Common {

    public final Rectangle screenshotRegion;
    public final Mat tpl;
    public final Mat screenshot;
    // offset for matched template origin relative to screenshot origin
    public final Point ssRelMatchPos;
    // center of matched template relative to screenshot origin
    public final Point ssRelCenterMatchPos;
    public final float matchValue;

    public Match(Rectangle screenshotRegion, Mat tpl, RobotClient bot, Mat screenshot, Point ssRelMatchPos,
        float matchValue) {
        super();
        this.screenshotRegion = screenshotRegion;
        this.tpl = tpl;
        this.bot = bot;
        this.screenshot = screenshot;
        this.ssRelMatchPos = ssRelMatchPos;
        this.ssRelCenterMatchPos = new Point(ssRelMatchPos.x + tpl.cols() / 2, ssRelMatchPos.y + tpl.rows() / 2);
        this.matchValue = matchValue;
    }

    /**
     * Click relative to origin of screenshot area.
     */
    public void clickRelToSsOrigin(Point p) throws AWTException, InterruptedException, IOException {
        super.click(ssOriginToAbsCoords(p));
    }

    /**
     * Click relative to origin of screenshot area.
     */
    public void clickRelToSsOrigin(int x, int y) throws AWTException, InterruptedException, IOException {
        clickRelToSsOrigin(new Point(x, y));
    }

    /**
     * Click relative to origin of matched area.
     */
    public void clickRelToMatchOrigin(Point p) throws AWTException, InterruptedException, IOException {
        super.click(matchOriginToAbsCoords(p));
    }

    /**
     * Click relative to origin of matched area.
     */
    public void clickRelToMatchOrigin(int x, int y) throws AWTException, InterruptedException, IOException {
        clickRelToMatchOrigin(new Point(x, y));
    }

    /**
     * Click at center of matched area.
     */
    public void clickMatchCenter() throws AWTException, InterruptedException, IOException {
        super.click(getAbsoluteMatchCenterPos());
    }

    /**
     * Click relative to center of matched area.
     */
    public void clickRelToMatchCenter(Point p) throws AWTException, InterruptedException, IOException {
        super.click(matchCenterToAbsCoords(p));
    }

    /**
     * Click relative to center of matched area.
     */
    public void clickRelToMatchCenter(int x, int y) throws AWTException, InterruptedException, IOException {
        clickRelToMatchCenter(new Point(x, y));
    }

    /**
     * Right-click relative to origin of screenshot area.
     */
    public void rightclickRelToSsOrigin(Point p) throws AWTException, InterruptedException, IOException {
        super.rightclick(ssOriginToAbsCoords(p));
    }

    /**
     * Right-click relative to origin of screenshot area.
     */
    public void rightclickRelToSsOrigin(int x, int y) throws AWTException, InterruptedException, IOException {
        rightclickRelToSsOrigin(new Point(x, y));
    }

    /**
     * Right-Click relative to origin of matched area.
     */
    public void rightclickRelToMatchOrigin(Point p) throws AWTException, InterruptedException, IOException {
        super.rightclick(matchOriginToAbsCoords(p));
    }

    /**
     * Right-click relative to origin of matched area.
     */
    public void rightclickRelToMatchOrigin(int x, int y) throws AWTException, InterruptedException, IOException {
        rightclickRelToMatchOrigin(new Point(x, y));
    }

    /**
     * Right-click center of matched area.
     */
    public void rightclickMatchCenter() throws AWTException, InterruptedException, IOException {
        super.rightclick(getAbsoluteMatchCenterPos());
    }

    /**
     * Right-click center of matched area.
     */
    public void rightclickRelToMatchCenter(Point p) throws AWTException, InterruptedException, IOException {
        super.rightclick(matchCenterToAbsCoords(p));
    }

    /**
     * Right-click center of matched area.
     */
    public void rightclickRelToMatchCenter(int x, int y) throws AWTException, InterruptedException, IOException {
        rightclickRelToMatchCenter(new Point(x, y));
    }

    public Rectangle offsetRegion(int x, int y, int width, int height) {
        return new Rectangle(screenshotRegion.x + ssRelMatchPos.x + x, screenshotRegion.y + ssRelMatchPos.y + y, width,
            height);
    }

    public Color getColorAt(int x, int y) {
        BytePointer ptr = screenshot.data();
        long pos = y * screenshot.cols() + x;
        int pixel = ptr.getInt(pos);
        return new Color(pixel, false);
    }

    public double pixelBlackDistance(int x, int y) {
        BytePointer ptr = screenshot.data();
        long pos = y * screenshot.cols() + x;
        int pixel = ptr.getInt(pos);
        return Math
            .sqrt((pixel & 0xff) * (pixel & 0xff) + (pixel & 0xff00) * (pixel & 0xff00)
                + (pixel & 0xff0000) * (pixel & 0xff0000));
    }

    public Point ssOriginToAbsCoords(Point p) {
        return new Point(screenshotRegion.x + p.x, screenshotRegion.y + p.y);
    }

    public Point matchOriginToAbsCoords(Point p) {
        return new Point(screenshotRegion.x + ssRelMatchPos.x + p.x, screenshotRegion.y + ssRelMatchPos.y + p.y);
    }

    public Point matchCenterToAbsCoords(Point p) {
        return new Point(screenshotRegion.x + ssRelCenterMatchPos.x + p.x,
            screenshotRegion.y + ssRelCenterMatchPos.y + p.y);
    }

    public Point matchCenterToAbsCoords(int x, int y) {
        return matchCenterToAbsCoords(new Point(x, y));
    }
    
    public Point getAbsoluteMatchCenterPos() {
        return ssOriginToAbsCoords(ssRelCenterMatchPos);
    }
}
