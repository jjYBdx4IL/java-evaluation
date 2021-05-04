package tests.javax.sound.sampled;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.Port;

public class MixerInfoTest {

    private static final Logger LOG = LoggerFactory.getLogger(MixerInfoTest.class);
    
    @Test
    public void testGetMixerInfo() throws LineUnavailableException {
        assumeTrue(Surefire.isSingleTestExecution());
        
        for (Info info : AudioSystem.getMixerInfo()) {
            LOG.info("" + info);
            for (Line.Info i : AudioSystem.getMixer(info).getSourceLineInfo()) {
                LOG.info("  < " + i);
            }
            for (Line.Info i : AudioSystem.getMixer(info).getTargetLineInfo()) {
                LOG.info("  > " + i);
            }
        }
        LOG.info("---");
        Mixer mixer = AudioSystem.getMixer(null);
        LOG.info("def mixer: " + mixer);
        Port port = (Port)mixer.getLine(Port.Info.LINE_OUT);
        LOG.info("line out: " + port);
        for (Control c : port.getControls()) {
            LOG.info("  " + c);
        }
    }
}
