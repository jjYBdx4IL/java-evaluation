package tests.javax.sound.midi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

/**
 * see http://www.onicos.com/staff/iz/formats/midi-event.html
 * 
 * @author jjYBdx4IL
 */
public class KeyboardSplitter implements Receiver {

    private final int[][] map;
    private final List<Receiver> receivers = new ArrayList<>(1);

    public KeyboardSplitter(int[][] map, Receiver... receivers) {
        this.map = map;
        if (map.length > 0 && map[0].length != 2) {
            throw new IllegalArgumentException("array must be of format {{split-offset-key, split-dest-channel}, ...}");
        }
        for (Receiver r : receivers) {
            this.receivers.add(r);
        }
    }

    public void add(Receiver rcvr) {
        receivers.add(rcvr);
    }

    public void send(MidiMessage msgIn, long timeStamp) {
        if (!(msgIn instanceof ShortMessage)) {
            _send(msgIn, timeStamp);
        }

        ShortMessage m = (ShortMessage) msgIn;

        int cmd = m.getCommand();
        if (cmd == ShortMessage.NOTE_ON || cmd == ShortMessage.NOTE_OFF) {
            int channel = m.getChannel();
            int note = m.getData1();
            int velocity = m.getData2();
            // find highest split below or equal to this note
            int maxSplitChannel = -1;
            int maxSplitNote = Integer.MIN_VALUE;
            for (int[] splitdef : map) {
                if (splitdef[0] <= note && splitdef[0] > maxSplitNote) {
                    maxSplitNote = splitdef[0];
                    maxSplitChannel = splitdef[1];
                }
            }
            if (maxSplitNote != Integer.MIN_VALUE) {
                try {
                    m.setMessage(m.getCommand(), maxSplitChannel, note, velocity);
                } catch (InvalidMidiDataException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        _send(msgIn, timeStamp);
    }

    private void _send(MidiMessage msg, long timeStamp) {
        for (Receiver r : receivers) {
            r.send(msg, timeStamp);
        }
    }

    @Override
    public void close() {
    }
}
