package org.imgscalr;

import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.awt.MdiAutoScaleFrame;
import com.github.jjYBdx4IL.utils.awt.MdiInternalImageFrame;
import org.imgscalr.Scalr.Method;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class ScalingTest {

    @Test
    public void testScaling() {
        BufferedImage img = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage()
            .getSubimage(1500, 1000, 800, 600);
        BufferedImage output = Scalr.resize(img, Method.ULTRA_QUALITY, 400, 300);

        MdiAutoScaleFrame frame = new MdiAutoScaleFrame("imgscalr test");
        frame.setPreferredSize(new Dimension(1400, 800));

        frame.add(create("input", img));
        frame.add(create("output", output));

        AWTUtils.showFrameAndWaitForCloseByUserTest(frame);
    }

    MdiInternalImageFrame create(String title, BufferedImage img) {
        MdiInternalImageFrame iframe = new MdiInternalImageFrame(title, img, false);
        iframe.setVisible(true);
        return iframe;
    }
}
