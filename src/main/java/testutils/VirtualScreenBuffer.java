package testutils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.github.jjYBdx4IL.utils.env.Maven;

/**
 * Used for graphics drawing units.
 *
 * @author jjYBdx4IL
 */
public class VirtualScreenBuffer {

    private final Dimension imageSize;
    private final Color bgColor;
    private final int imageType;
    // lazy init! do not create the bufferedimage inside the constructor call, otherwise we would initialize it twice when
    // injecting the permanent instance where the @VSBInject annotation is used
    private BufferedImage image = null;

    public VirtualScreenBuffer() {
        this(new Dimension(640, 480), Color.BLACK, BufferedImage.TYPE_INT_RGB);
    }

    public VirtualScreenBuffer(Dimension imageSize, Color bgColor, int imageType) {
        this.imageSize = imageSize;
        this.bgColor = bgColor;
        this.imageType = imageType;
        newImage();
    }

    private void initImg() {
        if (this.image == null) {
            this.image = new BufferedImage(imageSize.width, imageSize.height, imageType);
        }
    }

    public final void newImage() {
        getGraphics2D().setColor(bgColor);
        getGraphics2D().fillRect(0, 0, imageSize.width, imageSize.height);
    }

    public Graphics getGraphics() {
        initImg();
        return image.getGraphics();
    }

    public Graphics2D getGraphics2D() {
        return (Graphics2D) getGraphics();
    }

    public Dimension getSize() {
        return imageSize;
    }

    public void saveAndNew(Class<?> classRef, String saveName) throws IOException {
        File outDir = Maven.getTempTestDir(classRef);
        ImageIO.write(image, "png", new File(outDir, saveName + ".png"));
        newImage();
    }
}
