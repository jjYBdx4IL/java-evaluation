/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package com.hierynomus.sshj;

import static org.junit.Assert.*;

import com.coherentlogic.fred.FredClientTest;
import com.github.jjYBdx4IL.utils.vmmgmt.LibvirtUtils;
import com.github.jjYBdx4IL.utils.vmmgmt.OS;
import com.github.jjYBdx4IL.utils.vmmgmt.VMData;
import com.github.jjYBdx4IL.utils.vmmgmt.VMInstanceProviderRule;

import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SSHJTest {

    private static final Logger LOG = LoggerFactory.getLogger(SSHJTest.class);
    
    @ClassRule
    public static VMInstanceProviderRule providerRule = new VMInstanceProviderRule();
    
    static {
        if (!LibvirtUtils.isAvailable()) {
            providerRule = null;
        }
    }

    @Before
    public void before() {
        Assume.assumeNotNull(providerRule);
    }
    
    @Test
    public void test1() throws IOException {
        VMData vm = providerRule.getProvider().createVM(OS.UbuntuWilyAmd64);
        
        try (SSHClient ssh = new SSHClient()) {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            //ssh.loadKnownHosts();

            ssh.connect("localhost", vm.getSshForwardPort());
            //ssh.authPublickey(System.getProperty("user.name"));
            ssh.authPassword("root", "");
            
            // sftp file and data transfer
            try (SFTPClient sftp = ssh.newSFTPClient()) {
                String src = FredClientTest.class.getResource("application-context.xml").getFile();
                sftp.put(new FileSystemFile(src), "/tmp");
                sftp.put(src, "/tmp/2.xml");
                
                // structured remote file listings
                List<RemoteResourceInfo> rris = sftp.ls("/tmp");
                for (RemoteResourceInfo rri : rris) {
                    LOG.info(rri.toString());
                }
                
                // remote random read access
                RemoteFile rf = sftp.open("/tmp/2.xml");
                byte[] to = new byte[3];
                assertEquals(3, rf.read(2, to, 0, 3));
                assertEquals("xml", new String(to, "ASCII"));
                rf.close();
                
                // random write access
                Set<OpenMode> omode = new HashSet<>();
                omode.add(OpenMode.CREAT);
                omode.add(OpenMode.WRITE);
                omode.add(OpenMode.READ);
                omode.add(OpenMode.EXCL);
                rf = sftp.open("/tmp/3.xml", omode);
                rf.write(100, "abc".getBytes("ASCII"), 0, 3);
                assertEquals(3, rf.read(99, to, 0, 3));
                assertEquals("\0ab", new String(to, "ASCII"));
                rf.close();
            }
            
            try (Session session = ssh.startSession()) {
                Command cmd = session.exec("ls -lrt /tmp");
                LOG.info(IOUtils.readFully(cmd.getInputStream()).toString());
                LOG.info("start join");
                cmd.join(5, TimeUnit.SECONDS);
                LOG.info("\n** exit status: " + cmd.getExitStatus());
            }
            ssh.disconnect();
        }
    }

}
