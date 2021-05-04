/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package javazoom.jl;

import com.github.jjYBdx4IL.utils.env.Maven;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.Obuffer;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ConverterTest {

    private static final Logger LOG = LoggerFactory.getLogger(ConverterTest.class);
    public static File MP3FILE = new File(AdvancedPlayerTest.class.getResource("applause2.mp3").toExternalForm().substring(5));
    private static final File TEMP_DIR = Maven.getTempTestDir(ConverterTest.class);

    @Test
    public void test() throws JavaLayerException {
        Converter converter = new Converter();
        File wav = new File(TEMP_DIR, Converter.class.getName() + ".wav");
        converter.convert(MP3FILE.getAbsolutePath(), wav.getAbsolutePath(), new Converter.ProgressListener() {
            @Override
            public void converterUpdate(int updateID, int param1, int param2) {
                LOG.info("converterUpdate");
            }

            @Override
            public void parsedFrame(int frameNo, Header header) {
                LOG.info("parsed frame #" + frameNo);
            }

            @Override
            public void readFrame(int frameNo, Header header) {
                LOG.info("read frame #" + frameNo);
            }

            @Override
            public void decodedFrame(int frameNo, Header header, Obuffer o) {
                LOG.info("decoded frame #" + frameNo);
            }

            @Override
            public boolean converterException(Throwable t) {
                LOG.info("converterException");
                return true;
            }
        });
    }

}
