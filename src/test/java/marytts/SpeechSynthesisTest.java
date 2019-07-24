package marytts;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;

import org.junit.Test;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

/**
 * Inspired by:
 * https://github.com/marytts/marytts-txt2wav/blob/maven/src/main/java/de/dfki/mary/Txt2Wav.java
 * 
 *
 */
public class SpeechSynthesisTest implements LineListener {

    private volatile boolean finished = false;

    @Test
    public void test() throws LineUnavailableException, IOException, MaryConfigurationException, SynthesisException,
        InterruptedException {
        assumeTrue(Surefire.isSingleTestExecution());

        LocalMaryInterface mary = new LocalMaryInterface();
        AudioInputStream audio = mary.generateAudio("I am ready");

        final Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
        clip.open(audio);
        clip.addLineListener(this);
        clip.start();
        while (!finished) {
            Thread.sleep(50l);
        }
    }

    @Override
    public void update(LineEvent event) {
        finished = LineEvent.Type.STOP.equals(event.getType());
    }
}
