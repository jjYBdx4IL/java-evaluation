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
public class Promega3SetbfreeSplitter implements Receiver {

    private final int[][] map;
    private final List<Receiver> receivers = new ArrayList<>(1);

    // we don't use the lowest 3 keys on a 88 key keyboard in setBfree, so let's
    // use them
    // to select the organ keyboard which we want to adjust (A0 - pedals, A#0 -
    // lower keys, B0 - upper keys)
    private int lastControlNote = -1;
    private int lastController12Value = -1;

    public Promega3SetbfreeSplitter(int[][] map, Receiver... receivers) {
        this.map = map;
        if (map.length > 0 && map[0].length != 3) {
            throw new IllegalArgumentException(
                "array must be of format {{split-offset-key, split-dest-channel, transpose-by-n}, ...}");
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

        if (cmd == ShortMessage.CONTROL_CHANGE) {
            int channel = m.getChannel();
            int controller = m.getData1();
            int value = m.getData2();
            if (channel == 15 && controller == 12) {
                lastController12Value = value;
                return;
            }
            // value sent is too for this button for setBfree to recognize its
            // activation:
            if (controller == 91 && value != 0) {
                value = 64;
            }
            if (channel == 15 && controller == 44) {
                // adjust value range, promega 3 equalizer knobs have a range of
                // 54..74.
                value = Math.round((value - 54) * 127f / 20);
                if (lastControlNote == 21) {
                    // 2 draw bars for pedals, plus lowest draw bar for lower
                    // and upper keys
                    controller = 96 + lastController12Value;
                }
                if (lastControlNote == 22) {
                    // upper 8 draw bars for lower keys
                    controller = 104 + lastController12Value;
                }
                if (lastControlNote == 23) {
                    // upper 8 draw bars for upper keys
                    controller = 112 + lastController12Value;
                }
                try {
                    m.setMessage(m.getCommand(), 0, controller, value);
                } catch (InvalidMidiDataException e) {
                    throw new RuntimeException(e);
                }
            }
            // remap channel 3 to 0 because setBfree only cares about 0..2
            else if (channel == 3) {
                try {
                    // avoid clash with similar buttons for channel 0
                    m.setMessage(m.getCommand(), 0, controller + 1, value);
                } catch (InvalidMidiDataException e) {
                    throw new RuntimeException(e);
                }
            }

        } else if (cmd == ShortMessage.NOTE_ON || cmd == ShortMessage.NOTE_OFF) {
            int channel = m.getChannel();
            int note = m.getData1();
            int velocity = m.getData2();

            if (note < 24) {
                lastControlNote = note;
            }

            // find highest split below or equal to this note
            int maxSplitChannel = -1;
            int maxSplitNote = Integer.MIN_VALUE;
            int maxSplitTranspose = 0;
            for (int[] splitdef : map) {
                if (splitdef[0] <= note && splitdef[0] > maxSplitNote) {
                    maxSplitNote = splitdef[0];
                    maxSplitChannel = splitdef[1];
                    maxSplitTranspose = splitdef[2];
                }
            }
            if (maxSplitNote != Integer.MIN_VALUE) {
                try {
                    m.setMessage(m.getCommand(), maxSplitChannel, note + maxSplitTranspose, velocity);
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
