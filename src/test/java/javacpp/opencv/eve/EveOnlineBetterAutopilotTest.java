package javacpp.opencv.eve;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.github.jjYBdx4IL.utils.remoterobot.KeepAlive;
import com.privatejgoodies.common.base.SystemUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javacpp.opencv.Common;
import javacpp.opencv.Match;
import javacpp.opencv.Template;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import pushbullet.PushbulletUtils;
import testgroup.RequiresIsolatedVM;

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
public class EveOnlineBetterAutopilotTest extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(EveOnlineBetterAutopilotTest.class);

    private volatile boolean isActive = false;
    private volatile boolean isDoOnce = false;
    private static User32 lib = null;

    private static Template jumpTpl;
    private static Template dockTpl;
    private static Template undockTpl;
    private static Template lookAtMyShipTpl;

    KeepAlive keepAlive = null;
    
    @BeforeClass
    public static void beforeClass() throws IOException {
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
        lib = User32.INSTANCE;
    }

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

        new Thread() {
            @Override
            public void run() {
                final int hotKeyId = 1;
                final int hotKeyId2 = 2;
                WinUser.MSG msg = new WinUser.MSG();
                // assertTrue(lib.RegisterHotKey(null, hotKeyId,
                // WinUser.MOD_ALT, KeyEvent.VK_F12));
                // assertTrue(lib.RegisterHotKey(null, hotKeyId2,
                // WinUser.MOD_ALT, KeyEvent.VK_F11));
                assertTrue(lib.RegisterHotKey(null, hotKeyId, 0, KeyEvent.VK_F11));
                assertTrue(lib.RegisterHotKey(null, hotKeyId2, 0, KeyEvent.VK_F10));
                while (lib.GetMessage(msg, null, 0, 0) != -1) {
                    if (msg.message == WinUser.WM_QUIT) {
                        lib.UnregisterHotKey(null, hotKeyId);
                        lib.UnregisterHotKey(null, hotKeyId2);
                        return;
                    }
                    if (msg.message == WinUser.WM_HOTKEY) {
                        if (msg.wParam.intValue() == hotKeyId) {
                            isActive = !isActive;
                            LOG.info("changed state: isActive = " + isActive);
                            if (isActive) {
                                play("on.mp3");
                            } else {
                                play("off.mp3");
                            }
                        } else if (msg.wParam.intValue() == hotKeyId2) {
                            isDoOnce = true;
                            LOG.info("do once");
                        }
                    }
                }
                // assertTrue(lib.UnregisterHotKey(null, hotKeyId));
                // assertTrue(lib.UnregisterHotKey(null, hotKeyId2));
            }
        }.start();

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
}
