package jj.play.ns.nl.captcha;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import jj.play.ns.nl.captcha.Captcha;
import jj.play.ns.nl.captcha.text.producer.TextProducer;

import org.junit.Test;

/**
 * http://simplecaptcha.sourceforge.net/custom_images.html
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings("all")
public class SimpleCaptchaTest {

    @Test
    public void test1() throws IOException {
        TextProducer tp = new TextProducer() {

            @Override
            public String getText() {
                return "just a single line";
            }
        };
        Captcha captcha = new Captcha.Builder(200, 50)
                .addText(tp)
                .addText(tp)
                .build();
        BufferedImage bi = captcha.getImage();
        //ImageIO.write(bi, "png", new File("test.png"));
    }

    @Test
    public void test2() throws IOException {
        Captcha captcha = new Captcha.Builder(200, 50)
                .addText()
                .addBackground()
                .addNoise()
                .addBorder()
                .build();
        BufferedImage bi = captcha.getImage();
        //ImageIO.write(bi, "png", new File("test.png"));
    }

    @Test
    public void test3() throws IOException {
        BufferedImage b = new BufferedImage(400, 600, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = b.getGraphics();
        g.drawRect(100, 100, 100, 100);
        g.drawString("abc", 20, 20);
        //ImageIO.write(b, "png", new File("test.png"));
    }
}
