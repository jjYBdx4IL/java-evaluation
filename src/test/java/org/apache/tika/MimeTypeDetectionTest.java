/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.apache.tika;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.fit.cssbox.CssBoxTest;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class MimeTypeDetectionTest {

    @Test
    public void testJPEG() throws URISyntaxException, IOException {
        Tika tika = new Tika();
        File jpegFile = new File(
                new File(CssBoxTest.getLocalExampleHomepageRoot(), "cssbox_homepage_files"), "137");
        assertEquals("image/jpeg", tika.detect(jpegFile));
        assertEquals("application/rtf", tika.detect(new File("src/test/resources/message.rtf")));
    }
}
