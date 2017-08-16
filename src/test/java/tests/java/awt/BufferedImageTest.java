package tests.java.awt;

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
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class BufferedImageTest {

    @Test
    public void test2() throws IOException {
        BufferedImage b = new BufferedImage(400, 600, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = b.getGraphics();
        g.drawRect(100, 100, 100, 100);
        g.drawString("abc\r123", 20, 20);
        //ImageIO.write(b, "png", new File("test.png"));
    }
}
