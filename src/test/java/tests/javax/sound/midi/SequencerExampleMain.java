package tests.javax.sound.midi;

import com.github.jjYBdx4IL.parser.midi.MidiMessageParser;
import com.github.jjYBdx4IL.parser.midi.events.AllNotesOffMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Locale;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;

public class SequencerExampleMain implements MetaEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(SequencerExampleMain.class);

    public static void main(String[] args) throws Exception {
        new SequencerExampleMain().run();
    }

    public void run() throws Exception {

        Sequence sequence = MidiSystem.getSequence(new File(getClass().getResource("swallows.mid").toURI()));
//        sequence = MidiSystem.getSequence(new File("record.mid"));
        dumpInfo(sequence);
         sequence = MidiMessageParser.compactAndDuplicateChannels(sequence, 4);
//        sequence = MidiMessageParser.remapChannels(sequence, 0);

        Sequencer sequencer = MidiSystem.getSequencer(false);

        //MidiDevice outdev = DevSelUtils.getMidiOutDeviceHwVirtSw();
        MidiDevice outdev = DevSelUtils.getVirMidiOutDevice();
//        outdev = DevSelUtils.getHwOutDevice();
        outdev.open();
        Receiver receiver = outdev.getReceiver();

        receiver.send(AllNotesOffMsg.create(0), outdev.getMicrosecondPosition());
        receiver.send(AllNotesOffMsg.create(1), outdev.getMicrosecondPosition());
        receiver.send(AllNotesOffMsg.create(2), outdev.getMicrosecondPosition());
        receiver.send(AllNotesOffMsg.create(3), outdev.getMicrosecondPosition());

//        MidiChannelRemapper mapper = new MidiChannelRemapper(receiver);
//        mapper.mapAllTo(0, 3);
//        sequencer.getTransmitter().setReceiver(mapper);
        sequencer.getTransmitter().setReceiver(receiver);

        sequencer.getTransmitter().setReceiver(new MidiLoggerReceiver());

        sequencer.open();
        sequencer.addMetaEventListener(this);

        sequencer.setSequence(sequence);
        //sequencer.setTempoFactor(2.0f);
        sequencer.setLoopCount(1000);
        sequencer.start();
        while (System.in.available() == 0) {
            Thread.sleep(500L);
        }
        sequencer.stop();
        sequencer.close();

        outdev.close();
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
