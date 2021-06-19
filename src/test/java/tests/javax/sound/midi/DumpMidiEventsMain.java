package tests.javax.sound.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Transmitter;

/**
 *
 * @author jjYBdx4IL
 */
public class DumpMidiEventsMain {

    public static void main(String[] args) throws Exception {
        //MidiDevice dev = DevSelUtils.getHwInDevice();
        MidiDevice dev = DevSelUtils.getMidiInDeviceByName(".*virmidi.*hw:1,0,0.*|UM-ONE|MIDIIN4.*MPK249.*");
        Transmitter trans = dev.getTransmitter();
        trans.setReceiver(new MidiLoggerReceiver());
        dev.open();
        Thread.sleep(3600L * 1000L);
    }
}
