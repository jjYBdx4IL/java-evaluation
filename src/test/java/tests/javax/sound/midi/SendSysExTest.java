package tests.javax.sound.midi;

import static org.junit.Assert.assertNotNull;

import com.github.jjYBdx4IL.parser.midi.events.SystemExclusiveMsg;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

//@meta:keywords:akai,mpk249,mpk261,sysex,midi,dump@
public class SendSysExTest {

    private static final Logger LOG = LoggerFactory.getLogger(SendSysExTest.class);

    MidiDevice dev = null;
    Receiver receiver = null;
    
    public static void main(String[] args) throws Exception {
        new SendSysExTest().run();
    }
    
    public void run() throws Exception {
        dev = DevSelUtils.getMidiOutDeviceByName("MPK249");
        assertNotNull(dev);
        receiver = dev.getReceiver();

        dev.open();
        LOG.info("" + dev.getMicrosecondPosition());
        
        // http://practicalusage.com/akai-mpk261-mpk2-series-controlling-the-controller-with-sysex/
        // third byte seems to by the model ID: 24 for MPK249, 25 for MPK261
        // switch to preset #5 (1..30) (last byte in sequence)
        send("47 00 24 30 00 04 01 00 01 04");
        // this seems to work via the first logical device. However, sysex dumps seem to only work
        // via the 4th logical MPK MIDI device (MIDIIN4.*/MIDIOUT4.*).
        
        LOG.info("" + dev.getMicrosecondPosition());
    }
    
    private void send(MidiMessage msg) {
        receiver.send(msg, dev.getMicrosecondPosition());
    }
    
    private void send(String sysex) throws InvalidMidiDataException, DecoderException {
        sysex = "F0" + sysex + "F7";
        send(SystemExclusiveMsg.create(Hex.decodeHex(sysex.replaceAll(" ", ""))));
    }
    
}
