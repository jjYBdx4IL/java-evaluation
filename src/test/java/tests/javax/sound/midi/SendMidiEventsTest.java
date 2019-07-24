package tests.javax.sound.midi;

import com.github.jjYBdx4IL.parser.midi.events.AllNotesOffMsg;
import com.github.jjYBdx4IL.parser.midi.events.ControlChangeMsg;
import com.github.jjYBdx4IL.parser.midi.events.NoteOnMsg;
import com.github.jjYBdx4IL.parser.midi.events.ProgramChangeMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

/**
 *
 * @author jjYBdx4IL
 */
public class SendMidiEventsTest {

    private static final Logger LOG = LoggerFactory.getLogger(SendMidiEventsTest.class);

    MidiDevice dev = null;
    Receiver receiver = null;
    
    public static void main(String[] args) throws Exception {
        new SendMidiEventsTest().run();
    }
    
    public void run() throws Exception {
        dev = DevSelUtils.getMidiOutDeviceHwVirtSw();
        receiver = dev.getReceiver();

        dev.open();
        LOG.info("" + dev.getMicrosecondPosition());
        
        int channel = 0;
        
        send(ProgramChangeMsg.create(0, 5));
        send(ProgramChangeMsg.create(1, 5));
        send(ProgramChangeMsg.create(2, 5));
        send(ProgramChangeMsg.create(3, 5));
        send(ControlChangeMsg.create(0, 64, 64));
        
        
        send(NoteOnMsg.create(channel, "C3", 120));
        send(NoteOnMsg.create(channel, "E3", 120));
        send(NoteOnMsg.create(channel, "G3", 120));
        send(NoteOnMsg.create(channel, "C4", 120));
        send(NoteOnMsg.create(channel, "E4", 120));
        send(NoteOnMsg.create(channel, "G4", 120));
        send(NoteOnMsg.create(channel, "C5", 120));
        send(NoteOnMsg.create(channel, "E5", 120));
        send(NoteOnMsg.create(channel, "G5", 120));
        Thread.sleep(1L * 1000L);
        send(AllNotesOffMsg.create(0));

        
        
        send(NoteOnMsg.create(channel, "d3", 120));
        send(NoteOnMsg.create(channel, "f3", 120));
        send(NoteOnMsg.create(channel, "a3", 120));
        send(NoteOnMsg.create(channel, "d4", 120));
        send(NoteOnMsg.create(channel, "f4", 120));
        send(NoteOnMsg.create(channel, "a4", 120));
        send(NoteOnMsg.create(channel, "d5", 120));
        send(NoteOnMsg.create(channel, "f5", 120));
        send(NoteOnMsg.create(channel, "a5", 120));
        Thread.sleep(1L * 1000L);
        send(AllNotesOffMsg.create(0));
        send(ControlChangeMsg.create(0, 64, 0));
        
        LOG.info("" + dev.getMicrosecondPosition());
    }
    
    private void send(MidiMessage msg) {
        receiver.send(msg, dev.getMicrosecondPosition());
    }
    
}
