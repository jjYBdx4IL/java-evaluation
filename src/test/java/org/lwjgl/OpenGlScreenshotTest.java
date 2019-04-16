package org.lwjgl;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;

import com.github.jjYBdx4IL.utils.env.Maven;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

/**
 * Based on replies to: https://stackoverflow.com/questions/13819030/how-to-make-a-simple-screenshot-method-using-lwjgl
 *
 * @author jjYBdx4IL
 */
//@meta:keywords:screenshot@
public class OpenGlScreenshotTest extends SingleTriangle3dTest {
    private static final File TEMP_DIR = Maven.getTempTestDir(OpenGlScreenshotTest.class);

    @Override
    public void loopIteration() {
        super.loopIteration();
        
        try {
            screenShot();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void screenShot() throws IOException {
        //Creating an rbg array of total pixels
        int[] pixels = new int[getWidth() * getHeight()];
        int bindex;
        // allocate space for RBG pixels
        ByteBuffer fb = ByteBuffer.allocateDirect(getWidth() * getHeight() * 3);

        // grab a copy of the current frame contents as RGB
        glReadPixels(0, 0, getWidth(), getHeight(), GL_RGB, GL_UNSIGNED_BYTE, fb);

        BufferedImage imageIn = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        // convert RGB data in ByteBuffer to integer array
        for (int i=0; i < pixels.length; i++) {
            bindex = i * 3;
            pixels[i] =
                ((fb.get(bindex) << 16))  +
                ((fb.get(bindex+1) << 8))  +
                ((fb.get(bindex+2) << 0));
        }
        //Allocate colored pixel to buffered Image
        imageIn.setRGB(0, 0, getWidth(), getHeight(), pixels, 0 , getWidth());

        //Creating the transformation direction (horizontal)
        AffineTransform at =  AffineTransform.getScaleInstance(1, -1);
        at.translate(0, -imageIn.getHeight(null));

        //Applying transformation
        AffineTransformOp opRotated = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage imageOut = opRotated.filter(imageIn, null);

        ImageIO.write(imageOut, "png", new File(TEMP_DIR, "openglScreenshot.png"));
        //@insert:image:openglScreenshot.png@
    }    
}
