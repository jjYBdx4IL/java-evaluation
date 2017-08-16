/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.awt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class LoadFontFromJarTest {

    static final String testFontPath = "/net/sf/jasperreports/fonts/dejavu/DejaVuSansMono.ttf";

    @Test
    public void testLoadFontFromResource() throws IOException, FontFormatException {
        byte[] fontFileData;
        try (InputStream is = getClass().getResourceAsStream(testFontPath)) {
            fontFileData = IOUtils.toByteArray(is);
        }
        assertEquals(321524, fontFileData.length);

        Font font;
        try (InputStream is = new ByteArrayInputStream(fontFileData)) {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        }
        assertNotNull(font);
        assertEquals("DejaVu Sans Mono", font.getFontName());
        assertEquals(Font.PLAIN, font.getStyle());
    }

}
