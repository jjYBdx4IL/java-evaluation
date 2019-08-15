package tests.javax.sound.midi;

import com.github.jjYBdx4IL.parser.midi.MidiMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;

public class KeyboardSplitterMain implements MetaEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(KeyboardSplitterMain.class);

    public static void main(String[] args) throws Exception {
        new KeyboardSplitterMain().run();
    }

    public void run() throws Exception {
        try (MidiDevice outdev = DevSelUtils.getVirMidiOutDevice()) {
            outdev.open();

            KeyboardSplitter splitter = new KeyboardSplitter(new int[][] { { 0, 2 }, { 36, 1 }, { 60, 0 } },
                outdev.getReceiver());

            try (MidiDevice indev = DevSelUtils.getVirMidiInDevice()) {
                indev.open();
                indev.getTransmitter().setReceiver(splitter);
                Thread.sleep(10000000);
            }
        }
    }

    @Override
    public void meta(MetaMessage meta) {
        LOG.info(MidiMessageParser.toString(meta));
    }

}
