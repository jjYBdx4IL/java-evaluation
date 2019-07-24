package tests.javax.sound.sampled;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SineWaveSynthTest {

    private static final Logger LOG = LoggerFactory.getLogger(SineWaveSynthTest.class);

    public static final float SAMPLE_RATE = 44100f;
    public static final int CHANNELS = 2;
    public static final int SAMPLE_SIZE_BITS = 16;
    public static final boolean BIG_ENDIAN = true;
    public static final int FRAME_SIZE = SAMPLE_SIZE_BITS * CHANNELS / 8;
    public static final AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;

    public static final float HZ = 440f;
    public static final double VOLUME = 0.1;
    public static final long PLAY_DURATION_MS = 30000;
    public static final int BUFFER_DELAY_MS = 20;

    @Test
    public void testSinePlayback() throws LineUnavailableException, InterruptedException {
        assumeTrue(Surefire.isSingleTestExecution());

        beep(PLAY_DURATION_MS);
    }
    
    public static void beep(long ms) throws LineUnavailableException, InterruptedException {
        AudioFormat format = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            SAMPLE_RATE,
            SAMPLE_SIZE_BITS,
            CHANNELS,
            FRAME_SIZE,
            SAMPLE_RATE,
            BIG_ENDIAN);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        assertTrue(AudioSystem.isLineSupported(info));
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        LOG.info("default line buffer size: " + line.getBufferSize());
        line.open(format, (int) (BUFFER_DELAY_MS * FRAME_SIZE * SAMPLE_RATE / 1000));
        LOG.info("line buffer size: " + line.getBufferSize());
        line.start();

        long frame = 0;
        byte[] buf = new byte[FRAME_SIZE];
        long endTime = System.currentTimeMillis() + ms;
        
        while (System.currentTimeMillis() < endTime) {
            int avl = line.available();
            if (avl < FRAME_SIZE) {
                Thread.sleep(1);
                continue;
            }
 
            double amplitudeLeft = VOLUME * Math.sin(frame * 2 * Math.PI * HZ / SAMPLE_RATE);
            double amplitudeRight = VOLUME * Math.sin(frame * 2 * Math.PI * (2*HZ) / SAMPLE_RATE);

            toFrame(buf, amplitudeLeft, amplitudeRight);

            assertEquals(buf.length, line.write(buf, 0, buf.length));
            frame++;
        }

        LOG.info("line level: " + line.getLevel());
        line.close();
    }
    
    public static void beepAsync(long ms) {
        new Thread() {
            public void run() {
                try {
                    beep(ms);
                } catch (LineUnavailableException | InterruptedException e) {
                    LOG.error("", e);
                }
            };
        }.start();
    }
    
    public static void toFrame(byte[] buf, double leftAmp, double rightAmp) {
        long wordValue = (long) (leftAmp * 32767d);
        buf[0] = (byte) (wordValue >> 8);
        buf[1] = (byte) (wordValue & 0xFF);
        wordValue = (long) (rightAmp * 32767d);
        buf[2] = (byte) (wordValue >> 8);
        buf[3] = (byte) (wordValue & 0xFF);
    }
}
