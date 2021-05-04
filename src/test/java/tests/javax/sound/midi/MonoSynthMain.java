package tests.javax.sound.midi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.parser.midi.MidiMessageParser;
import com.github.jjYBdx4IL.parser.midi.events.PMidiMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.javax.sound.sampled.SampleUtils;

import java.util.Arrays;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class MonoSynthMain implements Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(MonoSynthMain.class);

    public static final float SAMPLE_RATE = 44100f;
    public static final int CHANNELS = 2;
    public static final int SAMPLE_SIZE_BITS = 16;
    public static final boolean BIG_ENDIAN = true;
    public static final int FRAME_SIZE = SAMPLE_SIZE_BITS * CHANNELS / 8;
    public static final AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;

    public static final float HZ = 440f;
    public static final double VOLUME = 0.1 / 127d;
    public static final int BUFFER_DELAY_MS = 20;

    SourceDataLine line = null;
    volatile ShortMessage currentNote = null;
    MidiDevice dev = null;
    volatile int[] program = new int[16];

    public MonoSynthMain() {
        Arrays.fill(program, 1);
    }

    public void send(MidiMessage msg, long timeStamp) {
        LOG.info(String.format("%,d: %s", timeStamp, MidiMessageParser.toString(msg)));
        if (msg instanceof ShortMessage) {
            ShortMessage m = (ShortMessage) msg;
            int cmd = m.getCommand();
            if (cmd == ShortMessage.NOTE_ON) {
                currentNote = m;
            }
            else if (cmd == ShortMessage.NOTE_OFF && currentNote != null && currentNote.getData1() == m.getData1()) {
                currentNote = null;
            }
            else if (cmd == ShortMessage.PROGRAM_CHANGE) {
                program[m.getChannel()] = m.getData1();
            }
        }
    }

    @Override
    public void close() {
    }

    private void initMidi() throws Exception {
        dev = DevSelUtils.getMidiInDeviceByName(".*virmidi.*hw:1,0,0.*|UM-ONE");
        Transmitter trans = dev.getTransmitter();
        trans.setReceiver(this);
        dev.open();
    }

    private void initAudio() throws Exception {
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

    private void run() throws Exception {
        initMidi();
        initAudio();

        long frame = 0;
        // we cannot transfer sample by sample because the processing overhead
        // would be too much
        final int maxXferFrames = line.getBufferSize() / FRAME_SIZE;
        LOG.info("max xfer frames: " + maxXferFrames);
        byte[] buf = new byte[maxXferFrames * FRAME_SIZE];

        OUTER: while (true) {
            int xferFrames = line.available() / FRAME_SIZE;
            if (xferFrames < 1) {
                Thread.sleep(1);
                continue;
            }

            for (int i = 0; i < xferFrames; i++) {
                final double secs = frame / SAMPLE_RATE;
                final double secs2pi = secs * 2 * Math.PI;

                double amplitudeLeft = 0;
                ShortMessage note = currentNote;
                if (note != null) {
                    // press lowest key on 88-key keyboard to exit
                    if (note.getData1() == 21) {
                        break OUTER;
                    }
                    int channel = note.getChannel();
                    float freq = PMidiMessage.getFrequency(note.getData1());
                    float vol = note.getData2();
                    int _program = program[channel];
                    
                    if (_program == 1) {
                        // sinus wave
                        amplitudeLeft = Math.sin(secs2pi * freq);
                    }
                    else if (_program == 2) {
                        // rectangular wave
                        amplitudeLeft = Math.sin(secs2pi * freq) >= 0 ? 1 : -1;
                    }
                    else if (_program == 3) {
                        // sawtooth wave
                        double wavePos = secs * freq;
                        wavePos -= Math.floor(wavePos);
                        if (wavePos < 0.25) {
                            amplitudeLeft = wavePos * 4;
                        }
                        else if (wavePos < 0.75) {
                            amplitudeLeft = 2 - wavePos * 4;
                        } else {
                            amplitudeLeft = wavePos * 4 - 4;
                        }
                    }

                    amplitudeLeft *= VOLUME * vol;
                }
                double amplitudeRight = amplitudeLeft;

                SampleUtils.toFrame(buf, i * FRAME_SIZE, amplitudeLeft, amplitudeRight);
                frame++;
            }
            assertEquals(xferFrames * FRAME_SIZE, line.write(buf, 0, xferFrames * FRAME_SIZE));
        }

        LOG.info("line level: " + line.getLevel());
        line.close();
        dev.close();
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        new MonoSynthMain().run();
    }
}
