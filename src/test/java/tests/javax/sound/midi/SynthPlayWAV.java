package tests.javax.sound.midi;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

import javazoom.jl.ConverterTest;
import javazoom.jl.converter.Converter;

/**
 *
 * @author jjYBdx4IL
 */
public class SynthPlayWAV {

    private static final Logger LOG = LoggerFactory.getLogger(SynthPlayWAV.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(SynthPlayWAV.class);
    private static final File WAV_FILE = new File(TEMP_DIR, SynthPlayWAV.class.getName() + ".wav");
    
    private File getWAVFile() throws Exception {
        if (WAV_FILE.exists()) {
            return WAV_FILE;
        }
        Converter conv = new Converter();
        conv.convert(ConverterTest.MP3FILE.getAbsolutePath(), WAV_FILE.getAbsolutePath());
        return WAV_FILE;
    }
    
    @Test
    public void testcrap() throws Exception {
        Synthesizer syn = MidiSystem.getSynthesizer();
        syn.open();
        syn.unloadAllInstruments(syn.getDefaultSoundbank());
        syn.loadAllInstruments(MidiSystem.getSoundbank(getWAVFile()));
        for (Instrument instr : syn.getLoadedInstruments()) {
            LOG.info(instr.toString());
        }
        Instrument instr = syn.getLoadedInstruments()[0];
        MidiChannel mc = syn.getChannels()[0];
        mc.programChange(instr.getPatch().getBank(), instr.getPatch().getProgram());
        mc.noteOn(48, 127);
        Thread.sleep(3000L);
        mc.allNotesOff();
    }
}
