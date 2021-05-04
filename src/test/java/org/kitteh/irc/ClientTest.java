/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.kitteh.irc;

import org.junit.Ignore;
import org.junit.Test;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.channel.ChannelJoinEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ClientTest {

    public static class Listener {

        @Handler
        public void onUserJoinChannel(ChannelJoinEvent event) {
            if (event.getClient().isUser(event.getUser())) { // It's me!
                event.getChannel().sendMessage("Hello world! Kitteh's here for cuddles.");
                return;
            }
            // It's not me!
            event.getChannel().sendMessage("Welcome, " + event.getUser().getNick() + "! :3");
        }
    }


    /**
     * twitch is working, just throwing a cosmetic exception because of server version missing
     * @throws InterruptedException
     */
    @Ignore
    @Test
    public void testTwitchIRC() throws InterruptedException {
        // Calling build() starts connecting.
        Client client = Client.builder().nick("r7vd2x4m").serverPassword("oauth:xyz").serverHost("irc.twitch.tv").build();
        client.getEventManager().registerEventListener(new Listener());
        client.addChannel("#incitatuslp");
        Thread.sleep(10000L);
    }
}
