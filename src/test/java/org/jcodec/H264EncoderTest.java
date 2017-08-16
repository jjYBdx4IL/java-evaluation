package org.jcodec;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.FileUtil;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class H264EncoderTest {

    final static int w = 800;
    final static int h = 480;

    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(H264EncoderTest.class.getName());
    protected final Tika tika = new Tika();

    @Test
    public void testSequenceEncoder() throws IOException {
        File outputFile = new File(FileUtil.getMavenTargetDir(), H264EncoderTest.class.getName() + ".test");
        SequenceEncoder enc = new SequenceEncoder(outputFile);

        for (int i = 1; i <= 3; i++) {
            Picture p = Picture.create(w, h, ColorSpace.RGB);
            enc.encodeNativeFrame(p);
        }
        enc.finish();
        assertEquals("video/quicktime", tika.detect(outputFile));
    }
}
