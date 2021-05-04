package tests.javax.sound.midi;

import java.util.Locale;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ListMidiDevicesTest {

    private static final Logger LOG = LoggerFactory.getLogger(ListMidiDevicesTest.class);

    @Test
    public void testListMIDIDevices() throws MidiUnavailableException {
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            LOG.info(String.format(Locale.ROOT, "%s/%s/%s/%s:",
                info.getVendor(), info.getName(), info.getVersion(), info.getDescription()));
            MidiDevice dev = MidiSystem.getMidiDevice(info);
            LOG.info(String.format("    receivers=%d transmitters=%d",
                dev.getMaxReceivers(), dev.getMaxTransmitters()));
            try {
                dev.getReceiver();
            } catch (MidiUnavailableException ex) {
                LOG.info("    no receiver (no output)");
            }
            try {
                dev.getTransmitter();
            } catch (MidiUnavailableException ex) {
                LOG.info("    no transmitter (no input)");
            }
        }
    }
}
