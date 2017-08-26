package tests.java.awt;

import static org.junit.Assert.assertFalse;

import java.awt.EventQueue;
import java.awt.FontFormatException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import com.github.jjYBdx4IL.utils.gfx.FontEffects;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import com.github.jjYBdx4IL.utils.junit4.Screenshot;

public class FontEffectsTest extends InteractiveTestBase {

    @Test
    public void testFontEffects() throws InterruptedException, InvocationTargetException, FontFormatException, IOException {
        openWindow();

        assertFalse(EventQueue.isDispatchThread());

        FontEffects effects = new FontEffects();
        effects.setText("blur shadow, no glass");
        effects.setShadowType(FontEffects.ShadowType.BLUR);
        effects.setGlassBG(false);
        effects.setShadowOffset(15);
        effects.paint();
        appendImage(effects.getImage());

        effects = new FontEffects();
        effects.setText("blur shadow, glass");
        effects.setShadowType(FontEffects.ShadowType.BLUR);
        effects.setGlassBG(true);
        effects.setShadowOffset(15);
        effects.paint();
        appendImage(effects.getImage());

        effects = new FontEffects();
        effects.setText("drop shadow, no glass");
        effects.setShadowType(FontEffects.ShadowType.DROP);
        effects.setGlassBG(false);
        effects.setShadowOffset(15);
        effects.paint();
        appendImage(effects.getImage());

        effects = new FontEffects();
        effects.setText("drop shadow, glass");
        effects.setShadowType(FontEffects.ShadowType.DROP);
        effects.setGlassBG(true);
        effects.setShadowOffset(15);
        effects.paint();
        appendImage(effects.getImage());

        Screenshot.takeDesktopScreenshot(FontEffectsTest.class.getName() + ".png", true);
        waitForWindowClosing();
    }
}
