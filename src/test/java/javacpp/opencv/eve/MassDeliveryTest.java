package javacpp.opencv.eve;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.github.jjYBdx4IL.utils.time.TimeUtils;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Date;

import eve.esi.ApiClientFactory;
import eve.esi.ApiWrapper;
import eve.esi.DataCache;
import javacpp.opencv.Common;
import javacpp.opencv.Match;
import javacpp.opencv.Template;
import net.troja.eve.esi.ApiClient;
import pushbullet.PushbulletUtils;
import testgroup.RequiresIsolatedVM;

@Category(RequiresIsolatedVM.class)
public class MassDeliveryTest extends Common {

    private static final Logger LOG = LoggerFactory.getLogger(MassDeliveryTest.class);
    
    Template jumpTpl;
    Template dockTpl;
    Template undockTpl;
    Template oreHoldTpl = null;
    Template oreHoldFullTpl = null;
    Template itemHangarTpl = null;
    Template nothingFoundTpl = null;

    ApiClient apiClient = null;
    DataCache cache = null;
    ApiWrapper api = null;
    
    @Test
    public void test() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());

        enableBot();
        if (bot.isLocal()) {
            bot.setAutoDelayMs(250);
            bot.setSendInputDelayMs(250);
        }
        
        apiClient = ApiClientFactory.getClient();
        cache = new DataCache(apiClient);
        api = new ApiWrapper(apiClient);
        
        cache.loadSystems();
        cache.loadStations();
        cache.loadStructures();
        
        jumpTpl = new Template("eve/EveOnlineBetterAutopilotJump.png", 0.93f, bot);
        dockTpl = new Template("eve/EveOnlineBetterAutopilotDock.png", 0.94f, bot);
        undockTpl = new Template("eve/EveOnlineBetterAutopilotUndock.png", 0.97f, bot);
        oreHoldFullTpl = new Template("eve/EveOnlineOreHoldFull.png", 0.985f, bot);
        itemHangarTpl = new Template("eve/EveOnlineItemHangar.png", 0.93f, bot);
        oreHoldTpl = new Template("eve/EveOnlineOreHold.png", 0.92f, bot);
        nothingFoundTpl = new Template("eve/NothingFound.png", 0.92f, bot);
        
        LOG.info("started");

        long fromLocationId = api.getCurrentLocationId();
        fromLocationId = cache.toLocationId("Mirilene VIII - Moon 9 - Duvolle Laboratories Factory");
        long toLocationId = cache.toLocationId("Vittenyn - VI Platform-1", true);
        
        LOG.info("from: " + fromLocationId);
        LOG.info("to: " + toLocationId);
        
        Date ping = new Date();
        boolean lastTrip = false;
        while (true) {
            Thread.sleep(1000);
            
            if (TimeUtils.isOlderThan(ping, "5m")) {
                PushbulletUtils.sendMessage("stalling");
            }
            
            // check if we are docked
            Match match = undockTpl.findBestMatch(undockRegion);
            if (match != null) {
                long locId = api.getCurrentLocationId();
                if (locId == fromLocationId) {
                    if (!moveItems(itemHangarTpl, oreHoldTpl)) {
                        Thread.sleep(5000);
                        continue;
                    }
                    
                    if (oreHoldEmpty()) {
                        LOG.info("ore hold still empty");
                        Thread.sleep(5000);
                        continue;
                    }
                    
                    api.setAutopilot(toLocationId);
                    
                    if (itemHangarEmpty()) {
                        LOG.info("last trip - item hangar is empty");
                        lastTrip = true;
                    }
                    
                    // undock
                    match.clickMatchCenter();
                    Thread.sleep(7000);
                }
                else if (locId == toLocationId) {
                    if (!moveItems(oreHoldTpl, itemHangarTpl)) {
                        Thread.sleep(5000);
                        continue;
                    }
                    
                    if (!oreHoldEmpty()) {
                        LOG.info("failed to empty ore hold");
                        Thread.sleep(5000);
                        continue;
                    }
                    
                    if (!lastTrip) {
                        api.setAutopilot(fromLocationId);
                        // undock
                        match.clickMatchCenter();
                        Thread.sleep(7000);
                    } else {
                        PushbulletUtils.sendMessage("Done.");
                    }
                } else {
                    PushbulletUtils.sendMessage("docked in wrong station");
                    ping = new Date();
                }
                continue;
            }

            if (!activateContextMenuEntry(FIRST_WAYPOINT_POS, 1, false, jumpTpl, dockTpl)) {
                if (!activateContextMenuEntry(FIRST_WAYPOINT_2NDPOS, 1, false, jumpTpl, dockTpl)) {
                    continue;
                }
            }

            Thread.sleep(6000);
            ping = new Date();
        }
    }

    private boolean oreHoldEmpty() throws IOException, AWTException, InterruptedException {
        Match m = oreHoldTpl.findBestMatch(inventoryRegion);
        if (m == null) {
            return false;
        }
        m.clickMatchCenter();
        Thread.sleep(1000);
        return nothingFoundTpl.findBestMatch(inventoryRegion) != null;
    }

    private boolean itemHangarEmpty() throws IOException, AWTException, InterruptedException {
        Match m = itemHangarTpl.findBestMatch(inventoryRegion);
        if (m == null) {
            return false;
        }
        m.clickMatchCenter();
        Thread.sleep(1000);
        return nothingFoundTpl.findBestMatch(inventoryRegion) != null;
    }

    private boolean moveItems(Template from, Template to) throws AWTException, IOException, InterruptedException {
        // activate source container
        Match fromMatch = from.findBestMatch(inventoryRegion);
        if (fromMatch == null) {
            LOG.warn("source match not found: " + from.getDesc());
            return false;
        }
        fromMatch.clickMatchCenter();

        // cut contents
        bot.sendInput(KeyEvent.VK_TAB);
        bot.sendInput(KeyEvent.VK_TAB);
        bot.sendInput(KeyEvent.VK_CONTROL, KeyEvent.VK_A);
        bot.sendInput(KeyEvent.VK_CONTROL, KeyEvent.VK_X);

        // activate destination container
        Match toMatch = to.findBestMatch(inventoryRegion);
        if (toMatch == null) {
            LOG.warn("destination match not found: " + to.getDesc());
            return false;
        }
        toMatch.clickMatchCenter();

        // drop
        bot.sendInput(KeyEvent.VK_TAB);
        bot.sendInput(KeyEvent.VK_TAB);
        bot.sendInput(KeyEvent.VK_CONTROL, KeyEvent.VK_V);

        // dropping too much -> ack
        bot.sendInput(KeyEvent.VK_ENTER);
        
        return true;
    }
}

