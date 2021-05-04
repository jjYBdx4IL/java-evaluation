package tests.javax.sound.midi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.parser.midi.MidiMessageParser;
import com.github.jjYBdx4IL.parser.midi.events.PMidiMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.javax.sound.sampled.SampleUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class MultiSynthMain implements Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(MultiSynthMain.class);

    public static final float SAMPLE_RATE = 44100f;
    public static final int CHANNELS = 2;
    public static final int SAMPLE_SIZE_BITS = 16;
    public static final boolean BIG_ENDIAN = true;
    public static final int FRAME_SIZE = SAMPLE_SIZE_BITS * CHANNELS / 8;
    public static final AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;

    public static final float HZ = 440f;
    public static final int BUFFER_DELAY_MS = 20;

    public static final double MAX_DISCONTINUITY = 0.003;
    public static final double MAX_VELOCITY = 127d;

    public static final boolean AUTOLIMIT = false;
    public static final double AUTOLIMITVOL = 1d;

    long frame = 0;
    SourceDataLine line = null;
    MidiDevice dev = null;
    ConcurrentLinkedDeque<ShortMessage> queue = new ConcurrentLinkedDeque<>();
    int[] program = new int[16];
    State[][] stateMtx = new State[16][128];
    LinkedHashSet<State> stateSet = new LinkedHashSet<>();
    double volume = 0.5;
    double targetVolume = volume;
    double MAX_VOLUME_STEP = Math.pow(2, -SAMPLE_SIZE_BITS + 1) * 16;

    static class State {
        final int channel;
        final int note;
        final int velocity;
        final int program;
        final long frameOffset;
        final float freq;
        final Envelope env;

        State(long frameOffset, int channel, int note, int velocity, int program) {
            this.frameOffset = frameOffset;
            this.channel = channel;
            this.note = note;
            this.velocity = velocity;
            this.program = program;
            this.freq = PMidiMessage.getFrequency(note);
            this.env = new Envelope();
        }
    }
    
    // envelope (for attack and release) is needed to avoid pop noises/artifacts
    // due to sudden changes in amplitudes/maximum wave values
    static class Envelope {
        double attackSecs = 0.1;
        double releaseSecs = 0.1;
        long startFrame = Long.MIN_VALUE;
        long offFrame = Long.MIN_VALUE;
        boolean finished = false;
        double releaseStartAmp = 0;
        
        Envelope() {
        }
        
        double getAmp(long frame) {
            if (isRelease()) {
                if (offFrame == Long.MIN_VALUE) {
                    offFrame = frame;
                }
                double secs = (frame - offFrame) / SAMPLE_RATE;
                double amp = releaseStartAmp - secs/releaseSecs;
                if (amp <= 0) {
                    finished = true;
                    return 0;
                }
                return amp;
            } else {
                if (startFrame == Long.MIN_VALUE) {
                    startFrame = frame;
                }
                double secs = (frame - startFrame) / SAMPLE_RATE;
                releaseStartAmp = Math.min(secs, attackSecs) / attackSecs; 
                return releaseStartAmp;
            }
        }
        
        void release(long frame) {
            offFrame = frame;
        }
        
        boolean isRelease() {
            return offFrame != Long.MIN_VALUE;
        }
        
        boolean isFinished() {
            return finished;
        }
    }

    public MultiSynthMain() {
        Arrays.fill(program, 1);
    }

    public void send(MidiMessage msg, long timeStamp) {
        LOG.info(String.format("%,d: %s", timeStamp, MidiMessageParser.toString(msg)));
        if (msg instanceof ShortMessage) {
            queue.offer((ShortMessage) msg);
        }
    }

    private void handleQueue() {
        while (!queue.isEmpty()) {
            ShortMessage m = queue.poll();
            int cmd = m.getCommand();
            int ch = m.getChannel();
            if (cmd == ShortMessage.NOTE_ON) {
                int note = m.getData1();
                if (stateMtx[ch][note] != null && !stateMtx[ch][note].env.isRelease()) {
                    LOG.warn("missing note off for: " + m);
                    stateMtx[ch][note].env.release(frame);
                }
                State s = new State(frame, ch, note, m.getData2(), program[ch]);
                stateMtx[ch][note] = s;
                stateSet.add(s);
            } else if (cmd == ShortMessage.NOTE_OFF) {
                int note = m.getData1();
                if (stateMtx[ch][note] == null || stateMtx[ch][note].env.isRelease()) {
                    LOG.warn("superfluous note off for: " + m + ", " + stateSet.size());
                } else {
                    stateMtx[ch][note].env.release(frame);
                    stateMtx[ch][note] = null;
                }
            } else if (cmd == ShortMessage.PROGRAM_CHANGE) {
                program[ch] = m.getData1();
            } else if (cmd == ShortMessage.CONTROL_CHANGE && (m.getData1() == 7 || m.getData1() == 1)) {
                targetVolume = m.getData2() / 127d;
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

        // we cannot transfer sample by sample because the processing overhead
        // would be too much
        final int maxXferFrames = line.getBufferSize() / FRAME_SIZE;
        LOG.info("max xfer frames: " + maxXferFrames);
        byte[] buf = new byte[maxXferFrames * FRAME_SIZE];

        while (true) {
            int xferFrames = line.available() / FRAME_SIZE;
            if (xferFrames < 1) {
                handleQueue();
                Thread.sleep(1);
                continue;
            }

            List<State> toRemove = new ArrayList<>();
            for (int i = 0; i < xferFrames; i++) {
                handleQueue();
                double amplitude = 0;
                for (State s : stateSet) {
                    final double secs = (frame - s.frameOffset) / SAMPLE_RATE;
                    final double secs2pi = secs * 2 * Math.PI;
                    double amp = 0d;
                    if (s.program == 1) {
                        // sinus wave
                        amp = Math.sin(secs2pi * s.freq);
                    } else if (s.program == 2) {
                        // rectangular wave
                        amp = Math.sin(secs2pi * s.freq) >= 0 ? 1 : -1;
                    } else if (s.program == 3) {
                        // sawtooth wave
                        double wavePos = secs * s.freq;
                        wavePos -= Math.floor(wavePos);
                        if (wavePos < 0.25) {
                            amp = wavePos * 4;
                        } else if (wavePos < 0.75) {
                            amp = 2 - wavePos * 4;
                        } else {
                            amp = wavePos * 4 - 4;
                        }
                    }
                    amplitude += amp * s.env.getAmp(frame) * s.velocity / MAX_VELOCITY;
                    if (s.env.finished) {
                        toRemove.add(s);
                    }
                }

                volume += Math.max(Math.min(targetVolume - volume, MAX_VOLUME_STEP), -MAX_VOLUME_STEP);

                amplitude *= volume;

                showClipping(amplitude);

                if (AUTOLIMIT) {
                    if (Math.abs(amplitude) > AUTOLIMITVOL) {
                        volume *= AUTOLIMITVOL / Math.abs(amplitude);
                        amplitude *= AUTOLIMITVOL / Math.abs(amplitude);
                    }
                }

                SampleUtils.toFrame(buf, i * FRAME_SIZE, amplitude, amplitude);
                frame++;

                if (!toRemove.isEmpty()) {
                    for (State s : toRemove) {
                        stateSet.remove(s);
                    }
                    toRemove.clear();
                }
            }
            assertEquals(xferFrames * FRAME_SIZE, line.write(buf, 0, xferFrames * FRAME_SIZE));
        }

        // LOG.info("line level: " + line.getLevel());
        // line.close();
        // dev.close();
    }

    long lastClipMessage = 0;

    private void showClipping(double amp) {
        if (Math.abs(amp) > 1d) {
            if (System.currentTimeMillis() >= lastClipMessage + 1000L) {
                LOG.warn(String.format("clipping, %+.1f", (Math.abs(amp) - 1) * 1e2));
                lastClipMessage = System.currentTimeMillis();
            }
        }
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        new MultiSynthMain().run();
    }
}
