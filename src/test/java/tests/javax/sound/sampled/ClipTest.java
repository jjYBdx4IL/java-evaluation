package tests.javax.sound.sampled;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.env.Surefire;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javazoom.jl.ConverterTest.MP3FILE;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
//@meta:keywords:audio clip playback,wav file playback,wav,wave,playback,simple@
public class ClipTest {

    private static final Logger LOG = LoggerFactory.getLogger(ClipTest.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(ClipTest.class);

    @Test
    public void test() throws JavaLayerException, InterruptedException {
        Assume.assumeTrue(Surefire.isSingleTestExecution());

        File wav = createWavFile();
        try {
            final Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));

            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        LOG.info("done");
                        clip.close();
                    }
                }
            });

            clip.open(AudioSystem.getAudioInputStream(wav));
            clip.start();
            waitForClip(clip);
            
            clip.open(AudioSystem.getAudioInputStream(wav));
            clip.setMicrosecondPosition(clip.getMicrosecondLength()/2);
            clip.start();
            waitForClip(clip);
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException exc) {
            LOG.error("", exc);
        }
    }

    private static void waitForClip(Clip clip) throws InterruptedException {
        synchronized (clip) {
            while (clip.isOpen()) {
                LOG.info(String.format(Locale.ROOT,
                        "%.1f/%.1f (%.1f%%)",
                        clip.getMicrosecondPosition() * 1e-6,
                        clip.getMicrosecondLength() * 1e-6,
                        clip.getMicrosecondPosition() * 1e2 / clip.getMicrosecondLength()));
                clip.wait(100L);
            }
        }
    }

    private static File createWavFile() throws JavaLayerException {
        Converter converter = new Converter();
        File wav = new File(TEMP_DIR, ClipTest.class.getName() + ".wav");
        converter.convert(MP3FILE.getAbsolutePath(), wav.getAbsolutePath());
        return wav;
    }

}
