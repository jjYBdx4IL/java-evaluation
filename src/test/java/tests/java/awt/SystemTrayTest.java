package tests.java.awt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.junit4.Screenshot;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SystemTrayTest {

    private final static Logger LOG = Logger.getLogger(SystemTrayTest.class.getName());
    private final static File TEMP_DIR = Maven.getTempTestDir(SystemTrayTest.class);
    @SuppressWarnings("unused")
    private final static File SCREENSHOT_DIR = new File(TEMP_DIR, "screenshots");
    private static Process p;

    private static Image createImage(Color color) {
        Dimension trayIconSize = SystemTray.getSystemTray().getTrayIconSize();
        final String text = "OSD";
        BufferedImage img = new BufferedImage(trayIconSize.width, trayIconSize.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) img.getGraphics();
        Font font = new Font("Dialog", Font.PLAIN, 12);
        FontMetrics fm = g.getFontMetrics(font);
        Color alpha = new Color(0, 0, 0, 255);
        g.setColor(alpha);
        g.fillRect(0, 0, trayIconSize.width, trayIconSize.height);
        g.setColor(color);
        g.setFont(font);
        g.drawString(text, 0, (trayIconSize.height - fm.getHeight()) / 2 + fm.getAscent());
        return img;
    }

    @BeforeClass
    public static void beforeClass() throws IOException, InterruptedException {
        assumeFalse(GraphicsEnvironment.isHeadless());
        if (!SystemTray.isSupported()) {
            LOG.info("no system tray support found, trying stalonetray...");
            try {
                ProcessBuilder bp = new ProcessBuilder("stalonetray");
                p = bp.start(); // throws exception if not installed
                long timeout = System.currentTimeMillis() + 60L * 1000L;
                while (!SystemTray.isSupported() && System.currentTimeMillis() < timeout) {
                    try {
                        p.exitValue();
                        break;
                    } catch (IllegalThreadStateException ex) {
                    }
                    Thread.sleep(1000L);
                }
            } catch (Exception ex) {
                LOG.info(ex.getMessage());
            }
            if (!SystemTray.isSupported()) {
                LOG.warn("tests will be skipped because we don't have system tray support!");
            } else {
                LOG.info("stalonetray started.");
            }
        }
    }

    @AfterClass
    public static void afterClass() {
        if (p != null) {
            p.destroy();
        }
    }

    @Test
    public void test() throws AWTException, InterruptedException {
        assumeTrue("system tray is supported", SystemTray.isSupported());

        PopupMenu popupMenu = new PopupMenu("main menu");
        popupMenu.add("label1");
        popupMenu.add("label2");
        MenuItem menuItem = new MenuItem();
        menuItem.setLabel("label 3");
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info(e);
                assertEquals("label 3", e.getActionCommand());
                assertEquals(ActionEvent.ACTION_PERFORMED, e.getID());
            }
        });
        popupMenu.add(menuItem);

        TrayIcon icon = new TrayIcon(createImage(Color.BLUE));
        icon.setToolTip("tooltip text");
        icon.setPopupMenu(popupMenu);
        SystemTray.getSystemTray().add(icon);
        icon.displayMessage("title", "text1", TrayIcon.MessageType.INFO);

        Thread.sleep(1000L);
        Screenshot.takeDesktopScreenshot(SystemTrayTest.class.getName() + ".1.png", true);
        icon.setImage(createImage(Color.RED));
        Thread.sleep(1000L);
        Screenshot.takeDesktopScreenshot(SystemTrayTest.class.getName() + ".2.png", true);

        SystemTray.getSystemTray().remove(icon);
    }
}
