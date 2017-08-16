package tests.java.awt;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.utils.awt.FontEffects;

import com.github.jjYBdx4IL.test.InteractiveTestBase;
import com.github.jjYBdx4IL.test.Screenshot;

import java.awt.EventQueue;
import java.awt.FontFormatException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

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
