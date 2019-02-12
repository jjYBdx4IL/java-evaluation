package org.imgscalr;

import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.awt.MdiAutoScaleFrame;
import com.github.jjYBdx4IL.utils.awt.MdiInternalImageFrame;
import com.github.jjYBdx4IL.utils.env.Maven;
import org.imgscalr.Scalr.Method;
import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;

public class ScalingTest {
    private static final File TEMP_DIR = Maven.getTempTestDir(ScalingTest.class);

    @Before
    public void before() {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }
    
    @Test
    public void testScaling() {
        BufferedImage img = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage()
            .getSubimage(1500, 1000, 800, 600);
        BufferedImage output = Scalr.resize(img, Method.ULTRA_QUALITY, 400, 300);

        MdiAutoScaleFrame frame = new MdiAutoScaleFrame("imgscalr test");
        frame.setPreferredSize(new Dimension(1400, 800));

        frame.add(create("input", img));
        frame.add(create("output", output));

        AWTUtils.showFrameAndWaitForCloseByUserTest(frame, new File(TEMP_DIR, "testScaling.png"));
        // @insert:image:testScaling.png@
    }

    MdiInternalImageFrame create(String title, BufferedImage img) {
        MdiInternalImageFrame iframe = new MdiInternalImageFrame(title, img, false);
        iframe.setVisible(true);
        return iframe;
    }
}
