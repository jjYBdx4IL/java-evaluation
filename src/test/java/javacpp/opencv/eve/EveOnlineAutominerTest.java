package javacpp.opencv.eve;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.env.Surefire;
import com.github.jjYBdx4IL.utils.time.TimeUtils;
import javacpp.opencv.Common;
import javacpp.opencv.Match;
import javacpp.opencv.Template;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pushbullet.PushbulletUtils;
import testgroup.RequiresIsolatedVM;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

/**
 * An example based on:
 * https://github.com/bytedeco/javacv/blob/master/samples/TemplateMatching.java
 * 
 * Requires 1920x1080 resolution and Eve Online client in English.
 */
// @meta:keywords:eve online@
@Category(RequiresIsolatedVM.class)
public class EveOnlineAutominerTest extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(EveOnlineAutominerTest.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(EveOnlineAutominerTest.class);
    private static final File LOG_FILE = new File(TEMP_DIR.getParentFile(), TEMP_DIR.getName() + ".log");
    private static final File CACHE_FILE = new File(TEMP_DIR.getParentFile(), TEMP_DIR.getName() + ".cache");
    private static final Properties CACHED_PROPS = new Properties();

    private boolean useLeftStripMiner = true;

    Template miningOverviewTpl = null;
    Template stripminer1Tpl = null;
    Template stripminerTpl = null;
    Template undockTpl = null;
    Template dockTpl = null;
    Template itemHangarTpl = null;
    Template oreHoldTpl = null;
    Template homeLocEntryTpl = null;
    // Template beltLocEntryTpl = null;
    Template selectAllTpl = null;
    Template warpToLoc0mTpl = null;
    Template warpTo0mTpl = null;
    Template oreHoldFullTpl = null;

    @BeforeClass
    public static void beforeClass() throws IOException {
        if (CACHE_FILE.exists()) {
            try (InputStream is = new FileInputStream(CACHE_FILE)) {
                CACHED_PROPS.load(is);
            }
        }
    }

    @Test
    public void test() throws IOException, InterruptedException, AWTException, FontFormatException {
        assumeTrue(Surefire.isSingleTestExecution());

        enableBot();

        Font baseFont1 = Font.createFont(Font.TRUETYPE_FONT, new File(
            "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Eve Online\\SharedCache\\ResFiles\\"
                + "88\\88f9d95bc6c159ab_0e00fd949bf65bc63e967002d7847113"));
        Font ctxMenuFont = baseFont1.deriveFont(Font.PLAIN, 13.0f); // 12.71
        Font baseFont2 = Font.createFont(Font.TRUETYPE_FONT, new File(
            "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Eve Online\\SharedCache\\ResFiles\\"
                + "bc\\bcf18b74b8808cb1_4bff95b968bbf6aec216b8a8d51553f7"));
        @SuppressWarnings("unused")
        Font titleFont = baseFont2.deriveFont(Font.PLAIN, 11.1f);

        BufferedImage warpTo0mImg = ReverseFontMatcherTest.createFontImage(ctxMenuFont, "Warp to Within 0 m", 0);
        warpTo0mTpl = new Template("warpTo0mImg", warpTo0mImg, 0.90f, bot);
        warpTo0mTpl.setBlur(true);

        miningOverviewTpl = new Template("eve/EveOnlineOverviewMining.png", 0.97f, bot);
        stripminer1Tpl = new Template("eve/EveOnlineStripMiner1b.png", 0.99f, bot);
        stripminerTpl = new Template("eve/EveOnlineStripMiner2.png", 0.99f, bot);
        undockTpl = new Template("eve/EveOnlineBetterAutopilotUndock.png", 0.98f, bot);
        dockTpl = new Template("eve/EveOnlineBetterAutopilotDock.png", 0.94f, bot);
        itemHangarTpl = new Template("eve/EveOnlineItemHangar.png", 0.93f, bot);
        oreHoldTpl = new Template("eve/EveOnlineOreHold.png", 0.92f, bot);
        homeLocEntryTpl = new Template("eve/EveOnlineHomeLoc.png", 0.84f, bot);
        // beltLocEntryTpl = new Template("eve/EveOnlineBeltLoc.png", 0.84f,
        // bot);
        selectAllTpl = new Template("eve/EveOnlineSelectAll.png", 0.95f, bot);
        warpToLoc0mTpl = new Template("eve/EveOnlineWarpTo0m.png", 0.95f, bot);
        oreHoldFullTpl = new Template("eve/EveOnlineOreHoldFull.png", 0.985f, bot);

        // width and height need to be a multiple of 2

        Rectangle stripminerRegion = new Rectangle(screenDim.width * 1 / 2, 0, screenDim.width / 2,
            screenDim.height * 2 / 3);

        undockTpl.findBestMatch(inventoryRegion);

        // check some stuff that must be always present in advance to catch
        // errors fast
        assertTrue(undockTpl.findBestMatch(undockRegion) != null || homeLocEntryTpl.findBestMatch() != null);
        // assertTrue(undockTpl.findBestMatch(undockRegion) != null ||
        // beltLocEntryTpl.findBestMatch() != null);
        assertTrue(undockTpl.findBestMatch(undockRegion) != null || miningOverviewTpl.findBestMatch() != null);
        assertTrue(oreHoldTpl.findBestMatch(inventoryRegion) != null);

        // activate ore hold so we can track progress
        oreHoldTpl.getLastMatch().clickMatchCenter();

        int state = 0;
        if (undockTpl.findBestMatch(undockRegion) != null) {
            state = 2;
        }
        LOG.info("starting in state " + state);
        Date lastStripMinerActivityDetected = new Date();
        Date lastWarpToBelt = new Date();
        long lastFullHoldMs = -1;
        int beltIdx = fromCache("beltIdx", 0);
        while (true) {
            Thread.sleep(1000);

            if (isShuttingDown() && state != 1) {
                state = 1;
                continue;
            }

            if (TimeUtils.isOlderThan(lastStripMinerActivityDetected, "30m")) {
                PushbulletUtils.sendMessage("timeout");
                if (state == 4) {
                    beltIdx = 0;
                    toCache("beltIdx", beltIdx);
                }
            }

            // we are in belt, let's mine away
            if (state == 0) {
                if (oreHoldFullTpl.findBestMatch(inventoryRegion) != null) {
                    if (lastFullHoldMs > 0) {
                        long durationMs = System.currentTimeMillis() - lastFullHoldMs;
                        String rttMsg = String.format("rtt = %,d ms", durationMs);
                        LOG.info(rttMsg);
                        FileUtils.write(LOG_FILE, rttMsg + "\n", StandardCharsets.UTF_8, true);
                    }
                    lastFullHoldMs = System.currentTimeMillis();

                    state++;
                    continue;
                }

                if (stripminerTpl.countMatches(stripminerRegion) == 2) {
                    LOG.info("still mining (1)");
                    lastStripMinerActivityDetected = new Date();
                    Thread.sleep(10000);
                    continue;
                }

                if (stripminer1Tpl.countMatches(stripminerRegion) == 2) {
                    LOG.info("still mining (2)");
                    lastStripMinerActivityDetected = new Date();
                    Thread.sleep(10000);
                    continue;
                }
                
                int numActiveStripMiners = stripminerTpl.getLastMatchCount() + stripminer1Tpl.getLastMatchCount();
                
                // launch drones early in case there are enemy NPCs around
                bot.sendInput(KeyEvent.VK_L);
                
                if (numActiveStripMiners == 0) {
                    Match match = miningOverviewTpl.findBestMatch(null);
                    if (match == null) {
                        LOG.warn("mining overview not found");
                        Thread.sleep(10000);
                        continue;
                    }
    
                    // no strip miner active for 8 minutes -> warp to next belt via
                    // HOME
                    if (TimeUtils.isOlderThan(lastStripMinerActivityDetected, "8m")
                        && TimeUtils.isOlderThan(lastWarpToBelt, "8m")) {
                        LOG.info("switching to next belt");
                        toCache("beltIdx", ++beltIdx);
                        state++;
                        continue;
                    }
    
                    if (!switchToMiningTab()) {
                        continue;
                    }
                    
                    match.clickRelToMatchOrigin(40, 70);
    
                    // approch first target in mining overview
                    bot.sendInput(KeyEvent.VK_Q);
                    // lock that target
                    bot.sendInput(KeyEvent.VK_CONTROL);
                    // wait for target lock
                    Thread.sleep(5000L);
                    // start mining drones
                    bot.sendInput(KeyEvent.VK_F);
                    
                    // start strip miners
                    if (useLeftStripMiner) {
                        bot.sendInput(KeyEvent.VK_F2);
                    } else {
                        bot.sendInput(KeyEvent.VK_F3);
                    }
                    useLeftStripMiner = !useLeftStripMiner;
                }
                
                if (useLeftStripMiner) {
                    bot.sendInput(KeyEvent.VK_F2);
                } else {
                    bot.sendInput(KeyEvent.VK_F3);
                }
                useLeftStripMiner = !useLeftStripMiner;
            }
            // set course to base
            else if (state == 1) {
                // call back drones
                bot.sendInput(KeyEvent.VK_R);
                Thread.sleep(15000);

                if (!activateContextMenuEntry(homeLocEntryTpl, null, dockTpl)) {
                    LOG.warn("HOME docking option not found");
                    Thread.sleep(10000);
                    continue;
                }

                if (isShuttingDown()) {
                    LOG.warn("stopping because servers are shutting down");
                    beltIdx = 0;
                    toCache("beltIdx", beltIdx);
                    throw new RuntimeException("stopping because servers are shutting down");
                }

                state++;
            }
            // wait until docked and move ores
            else if (state == 2) {
                Match undockMatch = undockTpl.findBestMatch(undockRegion);
                if (undockMatch == null) {
                    continue;
                }

                Thread.sleep(3000);

                // activate ore hold
                Match oreHoldMatch = oreHoldTpl.findBestMatch(inventoryRegion);
                if (oreHoldMatch == null) {
                    LOG.warn("ore hold not found");
                    Thread.sleep(10000);
                    continue;
                }

                oreHoldMatch.clickMatchCenter();
                bot.sendInput(KeyEvent.VK_TAB);
                bot.sendInput(KeyEvent.VK_TAB);
                bot.sendInput(KeyEvent.VK_CONTROL, KeyEvent.VK_A);
                bot.sendInput(KeyEvent.VK_CONTROL, KeyEvent.VK_X);

                // // find block border right to "ore hold" label in inventory
                // Point p = match.relMatchPos;
                // p.x += match.tpl.cols() + 3;
                // p.y += match.tpl.rows();
                // double distance = match.pixelBlackDistance(p.x, p.y);
                // for (int x = p.x; x < match.screenshot.cols(); x++) {
                // if (match.pixelBlackDistance(x, p.y) < distance / 2) {
                // // this should be the position of the
                // // first ore in the ore hold:
                // p.x = x + 30;
                // break;
                // }
                // }

                // match.rightclickRelToScreenshot(p);
                // Point firstOrePos = match.toAbsoluteCoords(p);

                // match = selectAllTpl.findBestMatchRetry(match.offsetRegion(0,
                // -230, 300, 500), 20, 3000);
                // if (match == null) {
                // Thread.sleep(10000);
                // continue;
                // }
                // match.clickCenter();

                Match itemHangarMatch = itemHangarTpl.findBestMatch(inventoryRegion);
                if (itemHangarMatch == null) {
                    LOG.warn("item hangar not found");
                    Thread.sleep(10000);
                    continue;
                }

                // drop into item hangar
                itemHangarMatch.clickMatchCenter();
                bot.sendInput(KeyEvent.VK_TAB);
                bot.sendInput(KeyEvent.VK_TAB);
                bot.sendInput(KeyEvent.VK_CONTROL, KeyEvent.VK_V);

                // check if ore hold has been emptied
                oreHoldMatch.clickMatchCenter();
                Thread.sleep(3000);
                if (oreHoldFullTpl.findBestMatch(inventoryRegion) != null) {
                    Thread.sleep(3000);
                    continue;
                }

                state++;
            }
            // undock
            else if (state == 3) {
                Match undockMatch = undockTpl.findBestMatch(undockRegion);
                if (undockMatch == null) {
                    Thread.sleep(3000);
                    continue;
                }

                undockMatch.clickMatchCenter();

                state++;
            }
            // warp to belt
            else if (state == 4) {
                Match m = miningOverviewTpl.findBestMatch();
                if (m == null) {
                    Thread.sleep(3000);
                    continue;
                }

                // click on belts tab
                if (!switchToBeltsTab()) {
                    continue;
                }

                // select and warp to asteroid
                if (!activateContextMenuEntry(m.matchCenterToAbsCoords(80, 64 + beltIdx * 19), warpTo0mTpl)) {
                    LOG.warn("warp option not found");
                    Thread.sleep(10000);
                    continue;
                }

                switchToMiningTab();

                Thread.sleep(45000);

                // rinse repeat
                lastWarpToBelt = new Date();
                state = 0;
            }
        }
    }
    
    private boolean switchToMiningTab() throws AWTException, InterruptedException, IOException {
        Match m = miningOverviewTpl.findBestMatch();
        if (m == null) {
            return false;
        }
        m.clickRelToMatchOrigin(80, 30);
        return true;
    }
    
    private boolean switchToBeltsTab() throws AWTException, InterruptedException, IOException {
        Match m = miningOverviewTpl.findBestMatch();
        if (m == null) {
            return false;
        }
        m.clickRelToMatchOrigin(165, 30);
        return true;
    }

    private static int fromCache(String id, int defaultValue) {
        if (CACHED_PROPS.containsKey(id)) {
            return Integer.parseInt(CACHED_PROPS.getProperty(id));
        }
        return defaultValue;
    }

    private static void toCache(String id, int value) throws IOException {
        CACHED_PROPS.setProperty(id, Integer.toString(value));
        try (OutputStream os = new FileOutputStream(CACHE_FILE)) {
            CACHED_PROPS.store(os, "");
        }
    }

}
