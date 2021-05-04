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
import javax.sound.sampled.SourceDataLine;

public class SineWaveSynthWithVolumeChangesTest {

    private static final Logger LOG = LoggerFactory.getLogger(SineWaveSynthWithVolumeChangesTest.class);

    public static final float SAMPLE_RATE = 44100f;
    public static final int CHANNELS = 2;
    public static final int SAMPLE_SIZE_BITS = 16;
    public static final boolean BIG_ENDIAN = true;
    public static final int FRAME_SIZE = SAMPLE_SIZE_BITS * CHANNELS / 8;
    public static final AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;

    public static final float HZ = 440f;
    public static double VOLUME = 0.1;
    public static final long PLAY_DURATION_MS = 30000;
    public static final int BUFFER_DELAY_MS = 20;

    private static SourceDataLine line = null;

    @Test
    public void testSinePlayback() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());

        beep(PLAY_DURATION_MS);
    }

    private static void initAudio() throws Exception {
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
        line = (SourceDataLine) AudioSystem.getLine(info);
        LOG.info("line: " + line.getLineInfo());
        LOG.info("default line buffer size: " + line.getBufferSize());
        line.open(format, (int) (BUFFER_DELAY_MS * FRAME_SIZE * SAMPLE_RATE / 1000));
        LOG.info("line buffer size: " + line.getBufferSize());
        line.start();
    }

    public static void beep(long ms) throws Exception {
        initAudio();

        long frame = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + ms;

        // we cannot transfer sample by sample because the processing overhead
        // would be too much
        final int maxXferFrames = line.getBufferSize() / FRAME_SIZE;
        LOG.info("max xfer frames: " + maxXferFrames);
        byte[] buf = new byte[maxXferFrames * FRAME_SIZE];
        double currentVolume = VOLUME;
        double targetVolume = VOLUME;
        double prevAmplitude = 0d;

        while (System.currentTimeMillis() < endTime) {
            int xferFrames = line.available() / FRAME_SIZE;
            if (xferFrames < 1) {
                Thread.sleep(1);
                continue;
            }
            for (int i = 0; i < xferFrames; i++) {
                double amplitude = Math.sin(frame * 2 * Math.PI * HZ / SAMPLE_RATE);

                // change volume when passing the 0 amplitude
                targetVolume = VOLUME * ((System.currentTimeMillis() - startTime) / 1000L % 2 == 1 ? .1d : 1d);
                if (targetVolume != currentVolume) {
                    if (prevAmplitude * amplitude < 0d) {
                        currentVolume = targetVolume;
                    }
                }
                
                SampleUtils.toFrame(buf, i * FRAME_SIZE, currentVolume * amplitude, currentVolume * amplitude);
                frame++;
                prevAmplitude = amplitude;
            }
            assertEquals(xferFrames * FRAME_SIZE, line.write(buf, 0, xferFrames * FRAME_SIZE));
        }

        LOG.info("line level: " + line.getLevel());
        line.close();
    }

    public static void beepAsync(long ms) {
        new Thread() {
            public void run() {
                try {
                    beep(ms);
                } catch (Exception e) {
                    LOG.error("", e);
                }
            };
        }.start();
    }
}
