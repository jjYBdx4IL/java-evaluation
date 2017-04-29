/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package javazoom.jl;

import com.github.jjYBdx4IL.utils.env.Surefire;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class AdvancedPlayerTest {

    private static final Logger LOG = LoggerFactory.getLogger(AdvancedPlayerTest.class);
    private int stoppedOnFrame = 0;

    @Test
    public void test() throws JavaLayerException, URISyntaxException, FileNotFoundException {
        Assume.assumeTrue(Surefire.isSingleTestExecution());

        AdvancedPlayer player = getPlayer();
        player.play(0, 20);
        LOG.info("" + stoppedOnFrame);
        player = getPlayer();
        player.play(stoppedOnFrame, Integer.MAX_VALUE);
        LOG.info("" + stoppedOnFrame);
    }

    private AdvancedPlayer getPlayer() throws FileNotFoundException, JavaLayerException, URISyntaxException {
        AdvancedPlayer player = new AdvancedPlayer(AdvancedPlayerTest.class.getResourceAsStream("applause2.mp3"));
        player.setPlayBackListener(new PlaybackListener() {

            @Override
            public void playbackFinished(PlaybackEvent event) {
                LOG.info("" + event.getFrame());
                stoppedOnFrame = event.getFrame(); // always returns 0
            }
        });
        return player;
    }
}
