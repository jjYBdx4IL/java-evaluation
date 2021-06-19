package tests.javax.sound.midi;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

public class DevSelUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DevSelUtils.class);

    public static final File fluidsf2File = new File("/usr/share/sounds/sf2/FluidR3_GM.sf2");
    
    public static Synthesizer getSynth() throws MidiUnavailableException, InvalidMidiDataException, IOException {
        Synthesizer syn = MidiSystem.getSynthesizer();
        syn.open();
        LOG.info("synthesizer: " + syn.getDeviceInfo());
        LOG.info("default soundbank: " + syn.getDefaultSoundbank());
        if (fluidsf2File.exists()) {
            LOG.info("using " + fluidsf2File);
            Soundbank bank = MidiSystem.getSoundbank(fluidsf2File);
            
            if (!syn.loadAllInstruments(bank)) {
                throw new RuntimeException("failed to import instruments from " + fluidsf2File);
            }
        } else {
            LOG.info("falling back to default soundbank");
        }
        int i = 0;
        for (Instrument instr : syn.getAvailableInstruments()) {
            LOG.info(String.format("INSTR(%d): %s", i++, instr.toString()));
        }
        return syn;
    }
    
    public static Instrument getInstrumentByName(Synthesizer syn, String name) throws MidiUnavailableException {
        Instrument i = null;
        for (Instrument instr : syn.getAvailableInstruments()) {
            if (instr.getName().trim().equals(name)) {
                i = instr;
            }
        }
        if (i == null) {
            throw new RuntimeException("instrument " + name + " not found");
        }
        return i;
    }

    public static MidiDevice getSwOutDevice() throws MidiUnavailableException {
        return getMidiOutDeviceByName("gervill");
    }

    public static MidiDevice getHwOutDevice() throws MidiUnavailableException {
        return getMidiOutDeviceByName("(?!.*(virmidi|gervill)).*");
    }
    
    public static MidiDevice getHwInDevice() throws MidiUnavailableException {
        return getMidiInDeviceByName("(?!.*(virmidi|gervill)).*");
    }
    
    // use snd-virmidi module on linux to interoperate with JACK and other software MIDI devices
    public static MidiDevice getVirMidiOutDevice() throws MidiUnavailableException {
        return getMidiOutDeviceByName("virmidi.*");
    }
    public static MidiDevice getVirMidiInDevice() throws MidiUnavailableException {
        return getMidiInDeviceByName("virmidi.*");
    }
    
    public static MidiDevice getMidiOutDeviceByName(String regex) throws MidiUnavailableException {
        if (regex == null) {
            throw new IllegalArgumentException("midi out device name must not be null");
        }
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(toString(info));
            }
            if (!info.getName().toLowerCase(Locale.ROOT).matches(regex.toLowerCase(Locale.ROOT))) {
                continue;
            }
            try {
                MidiDevice dev = MidiSystem.getMidiDevice(info);
                if (dev.getMaxTransmitters() != 0) {
                    continue;
                }
                if (dev.getMaxReceivers() != 0) {
                    LOG.info("out device selected: " + toString(info));
                    return dev;
                }
            } catch (MidiUnavailableException ex) {
            }
        }
        throw new MidiUnavailableException("midi out device not found: " + regex);
    }
    
    public static MidiDevice getMidiInDeviceByName(String regex) throws MidiUnavailableException {
        if (regex == null) {
            throw new IllegalArgumentException("midi in device name must not be null");
        }
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(toString(info));
            }
            if (!info.getName().toLowerCase(Locale.ROOT).matches(regex.toLowerCase(Locale.ROOT))) {
                continue;
            }
            try {
                MidiDevice dev = MidiSystem.getMidiDevice(info);
                if (dev.getMaxReceivers() != 0) {
                    continue;
                }
                if (dev.getMaxTransmitters() != 0) {
                    LOG.info("in device selected: " + toString(info));
                    return dev;
                }
            } catch (MidiUnavailableException ex) {
            }
        }
        throw new MidiUnavailableException("midi in device not found: " + regex);
    }
    
    /**
     * Get a midi out device, prefer non-virmidi over virmidi, prefer virmidi over gervill.
     * 
     * @return the device
     * @throws MidiUnavailableException 
     */
    public static MidiDevice getMidiOutDeviceHwVirtSw() throws MidiUnavailableException {
        MidiDevice dev = null;
        try {
            dev = getHwOutDevice();
        } catch (MidiUnavailableException e) {
            try {
                dev = getVirMidiOutDevice();
            } catch (MidiUnavailableException e1) {
                if (SystemUtils.IS_OS_LINUX) {
                    LOG.warn("virmidi driver not found, load snd-virmidi to connect to JACK/ALSA midi applications");
                }
                dev = getSwOutDevice();
            }
        }
        return dev;
    }
    
    public static String toString(MidiDevice.Info info) {
        return String.format(Locale.ROOT, "%s/%s/%s/%s",
            info.getVendor(), info.getName(), info.getVersion(), info.getDescription());
    }
}
