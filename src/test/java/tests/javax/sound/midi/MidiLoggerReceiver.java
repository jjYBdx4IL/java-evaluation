package tests.javax.sound.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import com.github.jjYBdx4IL.parser.midi.MidiMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * see http://www.onicos.com/staff/iz/formats/midi-event.html
 * 
 * @author jjYBdx4IL
 */
public class MidiLoggerReceiver implements Receiver {
    
    private static final Logger LOG = LoggerFactory.getLogger(MidiLoggerReceiver.class);
    
    public MidiLoggerReceiver() {
    }
    
    public void send(MidiMessage msg, long timeStamp) {
        LOG.info(String.format("%,d: %s", timeStamp, MidiMessageParser.toString(msg)));
    }

    @Override
    public void close() {
    }
}
