package tests.javax.sound.midi;

import com.github.jjYBdx4IL.parser.midi.MidiMessageParser;
import com.github.jjYBdx4IL.parser.midi.events.HasChannel;
import com.github.jjYBdx4IL.parser.midi.events.PMidiMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

/**
 * see http://www.onicos.com/staff/iz/formats/midi-event.html
 * 
 * @author jjYBdx4IL
 */
public class MidiChannelRemapper implements Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(MidiChannelRemapper.class);

    private final int[] channelMap = new int[16];
    private final List<Receiver> receivers = new ArrayList<>(1);

    public MidiChannelRemapper(Receiver... receivers) {
        for (Receiver r : receivers) {
            this.receivers.add(r);
        }
    }

    public void add(Receiver rcvr) {
        receivers.add(rcvr);
    }
    
    public void mapAllTo(int firstDest, int lastDest) {
        for (int i=firstDest; i<=lastDest; i++) {
            mapAllTo(i);
        }
    }
    
    public void mapAllTo(int dest) {
        for (int i = 0; i < channelMap.length; i++) {
            channelMap[i] |= (1 << dest);
        }
    }

    public void send(MidiMessage msgIn, long timeStamp) {
        LOG.info(MidiMessageParser.toString(msgIn));
        if (msgIn instanceof MetaMessage) {
            _send(msgIn, timeStamp);
        } else {
            PMidiMessage parsedMsg = MidiMessageParser.parse(msgIn);
            if (parsedMsg == null) {
                LOG.error("failed to parse midi message: " + MidiMessageParser.toString(msgIn.getMessage()));
            } else if (parsedMsg instanceof HasChannel) {
                int channel = ((HasChannel) parsedMsg).getChannel();
                int map = channelMap[channel];
                for (int j = 0; j < 16; j++) { 
                    if ((map & (1 << j)) == 0) {
                        continue;
                    }
                    try {
                        ((HasChannel) parsedMsg).setChannel(j);
                    } catch (InvalidMidiDataException e) {
                        LOG.error("", e);
                    }
                    _send(parsedMsg.toMidiMessage(), timeStamp);                        
                }
            } else {
                _send(parsedMsg.toMidiMessage(), timeStamp);
            }
            
        }
    }

    private void _send(MidiMessage msg, long timeStamp) {
        for (Receiver r: receivers) {
            r.send(msg, timeStamp);
        }
    }
    
    @Override
    public void close() {
    }
}
