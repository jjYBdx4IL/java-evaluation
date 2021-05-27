package org.apache.sshd;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.junit.Ignore;
import org.junit.Test;

public class ApacheMinaSshClientTest {

    public static final int defaultTimeoutSeconds = 3;
    public static final String command = "ls -l";
    
    @Ignore
    @Test
    public void testName() throws Exception {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        
        try (ClientSession session = client.connect("test", "localhost", 24)
          .verify(defaultTimeoutSeconds, TimeUnit.SECONDS).getSession()) {
            //session.addPasswordIdentity("test");
            session.setKeyIdentityProvider(null);
            session.auth().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
            
            try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream(); 
              ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL)) {
                channel.setOut(responseStream);
                try {
                    channel.open().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
                    try (OutputStream pipedIn = channel.getInvertedIn()) {
                        pipedIn.write(command.getBytes());
                        pipedIn.flush();
                    }
                
                    channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 
                    TimeUnit.SECONDS.toMillis(defaultTimeoutSeconds));
                    String responseString = new String(responseStream.toByteArray());
                    System.out.println(responseString);
                } finally {
                    channel.close(false);
                }
            }
        } finally {
            client.stop();
        }
    }
}
