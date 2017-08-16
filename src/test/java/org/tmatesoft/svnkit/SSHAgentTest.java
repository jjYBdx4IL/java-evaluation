/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.tmatesoft.svnkit;

import com.jcraft.jsch.agentproxy.TrileadAgentProxy;
import com.trilead.ssh2.auth.AgentProxy;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.cli.SVNConsoleAuthenticationProvider;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationProvider;
import org.tmatesoft.svn.core.auth.SVNAuthentication;
import org.tmatesoft.svn.core.auth.SVNSSHAuthentication;
import org.tmatesoft.svn.core.internal.io.svn.SVNSSHConnector;
import org.tmatesoft.svn.core.internal.io.svn.SVNSSHPrivateKeyUtil;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SSHAgentTest implements ISVNAuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(SSHAgentTest.class);

    @Ignore
    @Test
    public void testSSHAgent() throws SVNException {
        TrileadAgentProxy p;
        SVNSSHPrivateKeyUtil u;
        SVNConsoleAuthenticationProvider pa;
        SVNSSHConnector ssh;
        DefaultSVNAuthenticationManager def;

        SVNURL url = SVNURL.create("svn+ssh", "svn", "localhost", 22, "/work/svn/repos", true);
        SVNRepository repository = SVNRepositoryFactory.create(url);
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
        authManager.setAuthenticationProvider(this);
        repository.setAuthenticationManager(authManager);
        log.info("Repository Root: " + repository.getRepositoryRoot(true));

        // https://svnkit.com/javadoc/org/tmatesoft/svn/core/wc/SVNClientManager.html
        SVNClientManager clientManager = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), authManager);
        SVNLogClient client = clientManager.getLogClient();
        log.info(url.toDecodedString());
        client.doList(url, SVNRevision.HEAD, SVNRevision.HEAD, false, false, new ISVNDirEntryHandler() {
            @Override
            public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException {
                log.info(dirEntry.toString());
            }
        });
    }

    @Override
    public SVNAuthentication requestClientAuthentication(String kind, SVNURL url, String realm, SVNErrorMessage errorMessage, SVNAuthentication previousAuth, boolean authMayBeStored) {
        try {
            url = SVNURL.create("svn+ssh", "mark", "localhost", 22, "/work/svn/repos", true);
        } catch (SVNException ex) {
            log.error("", ex);
        }
        final AgentProxy agentProxy = SVNSSHPrivateKeyUtil.createOptionalSSHAgentProxy();
        SVNAuthentication auth = SVNSSHAuthentication.newInstance(url.getUserInfo(), agentProxy, url.getPort(), url, false);
        return auth;
    }

    @Override
    public int acceptServerAuthentication(SVNURL url, String realm, Object certificate, boolean resultMayBeStored) {
        return ISVNAuthenticationProvider.ACCEPTED;
    }

}
