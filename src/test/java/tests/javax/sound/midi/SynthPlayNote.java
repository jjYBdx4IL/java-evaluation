package tests.javax.sound.midi;

import com.github.jjYBdx4IL.utils.env.Surefire;

import org.junit.After;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/**
 *
 * @author jjYBdx4IL
 */
public class SynthPlayNote {

    private static final Logger LOG = LoggerFactory.getLogger(SynthPlayNote.class);

    Synthesizer syn = null;
    
    public void setInstrumentByName(MidiChannel channel, String name)
            throws MidiUnavailableException {
        Instrument i = DevSelUtils.getInstrumentByName(syn, name);
        channel.programChange(i.getPatch().getBank(), i.getPatch().getProgram());
    }

    @Test
    public void test1() throws Exception {
        Assume.assumeTrue(Surefire.isSingleTestExecution());

        syn = DevSelUtils.getSynth();
        
        LOG.info("synth: " + syn.getDeviceInfo());
        syn.open();
        MidiChannel mc = syn.getChannels()[0];

        // violin
        setInstrumentByName(mc, "Violin");
        mc.noteOn(55, 127);
        mc.noteOn(60, 127);
        Thread.sleep(1000L);
        mc.allNotesOff();

        // laughing x2
        setInstrumentByName(mc, "Applause");
        mc.noteOn(55, 127);
        mc.noteOn(60, 127);
        Thread.sleep(2000L);
        mc.allNotesOff();
        
    }

    // Idea from http://www.informit.com/articles/article.aspx?p=20457
    @Test
    public void testSeashore() throws Exception {
        Assume.assumeTrue(Surefire.isSingleTestExecution());

        syn = MidiSystem.getSynthesizer();
        syn.open();
        MidiChannel mc1 = syn.getChannels()[0];
        MidiChannel mc2 = syn.getChannels()[1];
        MidiChannel mc3 = syn.getChannels()[2];

        setInstrumentByName(mc1, "Seashore");
        setInstrumentByName(mc2, "Seashore");
        setInstrumentByName(mc3, "Seashore");

        for (;;) {
            mc1.allNotesOff();
            mc1.noteOn(32, 127);
            Thread.sleep(3500);
            mc2.allNotesOff();
            mc2.noteOn(32, 127);
            Thread.sleep(1500);
            mc3.allNotesOff();
            mc3.noteOn(32, 127);
            Thread.sleep(3000);
        }
    }

    @After
    public void after() {
        if (syn != null) {
            syn.close();
            syn = null;
        }
    }
    
}
