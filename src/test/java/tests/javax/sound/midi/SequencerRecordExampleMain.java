package tests.javax.sound.midi;

import com.github.jjYBdx4IL.parser.midi.MidiMessageParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Locale;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Patch;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;

public class SequencerRecordExampleMain implements MetaEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(SequencerRecordExampleMain.class);

    public static void main(String[] args) throws Exception {
        new SequencerRecordExampleMain().run();
    }

    public void run() throws Exception {
        Sequencer sequencer = MidiSystem.getSequencer(false);

        sequencer.getTransmitter().setReceiver(new MidiLoggerReceiver());
        
        MidiDevice indev = DevSelUtils.getHwInDevice();
        indev.open();
        indev.getTransmitter().setReceiver(sequencer.getReceiver());

        Sequence sequence = new Sequence(Sequence.PPQ, 10, 4);
        
        sequencer.open();
        sequencer.addMetaEventListener(this);

        sequencer.setSequence(sequence);
        sequencer.recordEnable(sequence.getTracks()[0], 0);
        sequencer.recordEnable(sequence.getTracks()[1], 1);
        sequencer.recordEnable(sequence.getTracks()[2], 2);
        sequencer.recordEnable(sequence.getTracks()[3], 3);
        sequencer.startRecording();
        while (System.in.available() == 0) {
            Thread.sleep(500L);
        }
        sequencer.stop();
        sequencer.close();

        indev.close();
        
        MidiSystem.write(sequence, 1, new File("record.mid"));
        LOG.info("done.");
    }

    @Override
    public void meta(MetaMessage meta) {
        LOG.info(MidiMessageParser.toString(meta));
    }

    public static void dumpInfo(Sequence sequence) {
        for (Patch p : sequence.getPatchList()) {
            LOG.info("Patch: " + p);
        }
        for (Track t : sequence.getTracks()) {
            LOG.info("Track: " + t.size());
        }
        LOG.info(String.format(Locale.ROOT, "seqence length (s): %.3f", sequence.getMicrosecondLength() / 1e6));
    }

}
