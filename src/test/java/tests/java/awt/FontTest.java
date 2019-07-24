/*
 * #%L
 * Text2Image Convertor
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.awt;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JLabel;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.github.jjYBdx4IL.utils.awt.FontScanner;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import com.github.jjYBdx4IL.utils.junit4.Screenshot;

public class FontTest extends InteractiveTestBase {

    private static final File TEMP_DIR = Maven.getTempTestDir(FontTest.class);
    private static final Logger log = Logger.getLogger(FontTest.class.getName());
    private static final int MAX_COUNT = 100;

    @Test
    public void testShowAllFonts() throws IOException, InterruptedException, InvocationTargetException {
        openWindow();

        FontScanner fontScanner = new FontScanner();
        List<String> files = fontScanner.getFontFiles(
            SystemUtils.IS_OS_WINDOWS ? "C:\\Windows\\fonts" : "/usr/share/fonts");
        log.info("found " + files.size() + " font files");
        int count = 0;
        for (String fontFilePath : files) {
            if (++count > MAX_COUNT) {
                break;
            }
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontFilePath));
                final JLabel fontLabel = new JLabel(fontFilePath);
                fontLabel.setFont(font.deriveFont(Font.ITALIC | Font.BOLD, 24.0f));
                EventQueue.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        label.setText(null);
                        getContainer().add(fontLabel);
                        jf.pack();
                        jf.setLocationRelativeTo(null);
                    }
                });
            } catch (FontFormatException | IOException ex) {
                log.warn(ex.getMessage());
            }
        }

        Screenshot.takeDesktopScreenshot(FontTest.class.getName() + ".png", true);
        writeWindowAsPng(new File(TEMP_DIR, "FontTest.png"));
        // @insert:image:FontTest.png@
        waitForWindowClosing();
    }
}
