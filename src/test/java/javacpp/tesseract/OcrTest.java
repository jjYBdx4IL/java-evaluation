package javacpp.tesseract;

import static org.bytedeco.tesseract.global.tesseract.RIL_TEXTLINE;
import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.cache.SimpleDiskCacheEntry;
import com.github.jjYBdx4IL.utils.env.Maven;
import javacpp.opencv.LoadOrderDetectorTest;
import org.apache.poi.util.IOUtils;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.BOX;
import org.bytedeco.leptonica.BOXA;
import org.bytedeco.tesseract.TessBaseAPI;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import testgroup.RequiresIsolatedVM;

/**
 * inspired by the examples at:
 * https://github.com/bytedeco/javacpp-presets/blob/master/tesseract/samples/
 * 
 *
 */
@Category(RequiresIsolatedVM.class)
public class OcrTest {

    private static final Logger LOG = LoggerFactory.getLogger(OcrTest.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(OcrTest.class);
    public static final String TESSERACT_TRAINDATA_ENG_URL = "https://github.com/tesseract-ocr/tessdata/raw/4.00/eng.traineddata";

    static {
        LoadOrderDetectorTest.loadLibs("jnitesseract");
    }
    
    @Ignore
    @Test
    public void test() throws IOException {
        BufferedImage textImage = createImageFromText("This is a test.");

        ImageIO.write(textImage, "jpg", new File(TEMP_DIR, "test1.jpg"));

        // pixReadMem(data, size)

        try (InputStream is = SimpleDiskCacheEntry.inputStream(TESSERACT_TRAINDATA_ENG_URL)) {
            IOUtils.copy(is, new File(TEMP_DIR, "eng.traineddata"));
        }

        BytePointer outText;

        TessBaseAPI api = new TessBaseAPI();

        assertEquals(0, api.Init(TEMP_DIR.getAbsolutePath(), "eng"));

        int[] imgData = textImage.getRaster().getPixels(0, 0, textImage.getWidth(), textImage.getHeight(), (int[])null);
        byte[] byteData = new byte[imgData.length];
        for (int i=0; i<byteData.length; i++) {
            byteData[i] = (byte) (imgData[i] & 0xFF);
        }
        
        // PIX image = pixRead(file.getAbsolutePath());
        api.SetImage(byteData, textImage.getWidth(), textImage.getHeight(), 1, textImage.getWidth());
        
        int[] blockIds = {};
        BOXA boxes = api.GetComponentImages(RIL_TEXTLINE, true, null, blockIds);
        LOG.info("boxes count: " + boxes.n());
        
        for (int i = 0; i < boxes.n(); i++) {
            // For each image box, OCR within its area
            BOX box = boxes.box(i);
            api.SetRectangle(box.x(), box.y(), box.w(), box.h());
            outText = api.GetUTF8Text();
            String ocrResult = outText.getString();
            int conf = api.MeanTextConf();

            String boxInformation = String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d, confidence: %d, text: %s", i, box.x(), box.y(), box.w(), box.h(), conf, ocrResult);
            LOG.info(boxInformation);

            outText.deallocate();
        }        
        
        api.End();
        api.close();
    }

    public static BufferedImage createImageFromText(String text) {
        Font f = new Font("MONOSPACE", Font.PLAIN, 24);
        final int padding = 16;
        BufferedImage textImage = new BufferedImage(16, 16, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = (Graphics2D) textImage.getGraphics();
        g.setFont(f);
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);
        g.dispose();

        textImage = new BufferedImage(2 * padding + (int) bounds.getWidth(), 2 * padding + (int) bounds.getHeight(),
            BufferedImage.TYPE_BYTE_GRAY);
        g = (Graphics2D) textImage.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, textImage.getWidth(), textImage.getHeight());
        g.setFont(f);
        g.setColor(Color.black);
        g.drawChars(text.toCharArray(), 0, text.length(), padding, padding - (int) bounds.getMinY());
        g.dispose();
        return textImage;
    }
}
