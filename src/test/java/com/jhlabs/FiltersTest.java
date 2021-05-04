package com.jhlabs;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import com.jhlabs.image.ImageUtils;
import com.jhlabs.image.UnsharpFilter;
import com.jhlabs.image.WoodFilter;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FiltersTest extends InteractiveTestBase {

    @Test
    public void testWoodFilter() throws InterruptedException, InvocationTargetException {
        openWindow();

        int w = 400;
        int h = w * 9 / 16;
        BufferedImage img = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage();
        img = ImageUtils.getSubimage(img, 0, 0, w, h);
        append(img, "aestheticode");

        WoodFilter wood = new WoodFilter();

        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        BufferedImage img2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        wood.filter(img, img2);
        append(img2, "wood filter");

        UnsharpFilter unsharpen = new UnsharpFilter();
        unsharpen.filter(img2, img);
        append(img, "wood + unsharpen");

        saveWindowAsImage("wood");
        waitForWindowClosing();
    }

}
