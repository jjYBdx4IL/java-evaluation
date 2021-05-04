package tests.javax.sound.midi;

import com.github.jjYBdx4IL.parser.midi.MidiMessageParser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;

public class Promega3SetbfreeSplitterMain implements MetaEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(Promega3SetbfreeSplitterMain.class);

    public static void main(String[] args) throws Exception {
        new Promega3SetbfreeSplitterMain().run();
    }

    public void run() throws Exception {
        try (MidiDevice outdev = DevSelUtils.getVirMidiOutDevice()) {
            outdev.open();

            Promega3SetbfreeSplitter splitter = new Promega3SetbfreeSplitter(
                new int[][] { { 0, 2, 12 }, { 36, 1, 0 }, { 60, 0, -12 } },
                outdev.getReceiver());

            try (MidiDevice indev = DevSelUtils.getVirMidiInDevice()) {
                indev.open();
                indev.getTransmitter().setReceiver(splitter);
                System.out.println("Enter CTRL-D to stop.");
                IOUtils.toString(System.in, StandardCharsets.UTF_8);
            }
        }
    }

    @Override
    public void meta(MetaMessage meta) {
        LOG.info(MidiMessageParser.toString(meta));
    }

}
