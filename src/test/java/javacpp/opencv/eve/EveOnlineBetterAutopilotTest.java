package javacpp.opencv.eve;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.github.jjYBdx4IL.utils.remoterobot.KeepAlive;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import javacpp.opencv.Common;
import javacpp.opencv.Match;
import javacpp.opencv.Template;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pushbullet.PushbulletUtils;
import testgroup.RequiresIsolatedVM;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.KeyStroke;

/**
 * An example based on:
 * https://github.com/bytedeco/javacv/blob/master/samples/TemplateMatching.java
 * 
 * Requires 1920x1080 resolution and Eve Online client in English. Template
 * pictures were taken at:
 * <ul>
 * <li>dark matter
 * <li>1920x1080, max quality
 * <li>window blur, no transparency
 * <li>context menu font size 12
 * </ul>
 * 
 * Can be activated/deactivated by collapsing the system information in the
 * upper left corner.
 * 
 * <ul>
 * <li>Update: added ALT-F12 shortcut for activation/deactivation.
 * <li>Update: added activation/deactivation sounds.
 * <li>Update: added ALT-F11 shortcut for single activation
 * </ul>
 */
// @meta:keywords:eve online,autopilot@
@Category(RequiresIsolatedVM.class)
public class EveOnlineBetterAutopilotTest extends Common implements HotKeyListener {

    private static final Logger LOG = LoggerFactory.getLogger(EveOnlineBetterAutopilotTest.class);

    private volatile boolean isActive = false;
    private volatile boolean isDoOnce = false;

    private static Template jumpTpl;
    private static Template dockTpl;
    private static Template undockTpl;
    private static Template lookAtMyShipTpl;

    KeepAlive keepAlive = null;
    
    private final KeyStroke keyStrokeF10 = KeyStroke.getKeyStroke("F10");
    private final KeyStroke keyStrokeF11 = KeyStroke.getKeyStroke("F11");
    
    @Test
    public void test() throws IOException, InterruptedException, AWTException, JavaLayerException {
        assumeTrue(Surefire.isSingleTestExecution());

        enableBot();
        if (bot.isLocal()) {
            bot.setAutoDelayMs(250);
            bot.setSendInputDelayMs(250);
        }

        keepAlive = new KeepAlive();
        keepAlive.start();

        jumpTpl = new Template("eve/EveOnlineBetterAutopilotJump.png", 0.93f, bot);
        dockTpl = new Template("eve/EveOnlineBetterAutopilotDock.png", 0.94f, bot);
        undockTpl = new Template("eve/EveOnlineBetterAutopilotUndock.png", 0.97f, bot);
        lookAtMyShipTpl = new Template("eve/EveOnlineBetterAutopilotLookAtMyShip.png", 0.94f, bot);

        Provider provider = Provider.getCurrentProvider(false);
        provider.register(keyStrokeF10, this);
        provider.register(keyStrokeF11, this);
        
        Rectangle region = new Rectangle(FIRST_WAYPOINT_POS.x - 50, FIRST_WAYPOINT_POS.y - 50, 500, 500);

        LOG.info("started");

        int noActionCounter = 0;
        while (true) {
            Thread.sleep(1000);

            if (!isActive && !isDoOnce) {
                noActionCounter = 0;
                continue;
            }

            if (isDoOnce) {
                noActionCounter = 0;
                isDoOnce = false;
            }

            // check if we are docked
            Match match = undockTpl.findBestMatch(undockRegion);
            if (match != null) {
                LOG.info("docked");
                PushbulletUtils.sendMessage("docked in station");
                Thread.sleep(5000);
                continue;
            }

            rightclick(FIRST_WAYPOINT_POS.x, FIRST_WAYPOINT_POS.y);

            match = lookAtMyShipTpl.findBestMatch(region);
            if (match != null) {
                LOG.info("found lookAtMyShip match");
                rightclick(FIRST_WAYPOINT_POS.x, FIRST_WAYPOINT_POS.y + 15);
                Thread.sleep(DEFAULT_DELAY_MS);
            }

            match = jumpTpl.findBestMatch(region);
            if (match != null) {
                LOG.info("found jump match");
                match.clickMatchCenter();
                Thread.sleep(4000);
                noActionCounter = 0;
                continue;
            }

            match = dockTpl.findBestMatch(region);
            if (match != null) {
                LOG.info("found dock match");
                match.clickMatchCenter();
                Thread.sleep(5000);
                noActionCounter = 0;
                continue;
            }

            noActionCounter++;
            if (noActionCounter > 90) {
                PushbulletUtils.sendMessage("autopilot is stalling");
            }
        }
    }

    @Test
    public void testRepeatClick() throws IOException, InterruptedException, AWTException {
        assumeTrue(Surefire.isSingleTestExecution());

        LOG.info("started");

        while (true) {
            Thread.sleep(5000);
            click();
        }
    }

    public static void play(String resName) {
        new Thread() {
            public void run() {
                try {
                    AdvancedPlayer player = new AdvancedPlayer(
                        EveOnlineBetterAutopilotTest.class.getResourceAsStream(resName));
                    player.play();
                } catch (JavaLayerException e) {
                    LOG.error("", e);
                }
            }
        }.start();
    }

    @Override
    public void onHotKey(HotKey hotKey) {
        if (hotKey.keyStroke.equals(keyStrokeF11)) {
            isActive = !isActive;
            LOG.info("changed state: isActive = " + isActive);
            if (isActive) {
                play("on.mp3");
            } else {
                play("off.mp3");
            }
        } else if (hotKey.keyStroke.equals(keyStrokeF10)) {
            isDoOnce = true;
            LOG.info("do once");
        }
    }
}
