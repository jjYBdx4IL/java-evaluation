/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package com.hierynomus.sshj;

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
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

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

        final SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        //ssh.loadKnownHosts();

        ssh.connect("localhost", vm.getSshForwardPort());
        try {
            //ssh.authPublickey(System.getProperty("user.name"));
            ssh.authPassword("root", "");
            final Session session = ssh.startSession();
            try {
                final Command cmd = session.exec("ls -l /");
                System.out.println(IOUtils.readFully(cmd.getInputStream()).toString());
                cmd.join(5, TimeUnit.SECONDS);
                System.out.println("\n** exit status: " + cmd.getExitStatus());
            } finally {
                session.close();
            }
        } finally {
            ssh.disconnect();
        }
    }

}
