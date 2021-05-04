package org.eclipse.egit.github;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.cfg.AtomicPropsFileSimpleGui;
import com.github.jjYBdx4IL.utils.env.Surefire;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListRepositoriesTest {

    private static final Logger LOG = LoggerFactory.getLogger(ListRepositoriesTest.class);

    @Test
    public void test() throws IOException {
        assumeTrue(Surefire.isSingleTestExecution());

        AtomicPropsFileSimpleGui gui = new AtomicPropsFileSimpleGui(ListRepositoriesTest.class, "githubOauthToken");
        gui.loadOrShow(false);

        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(gui.get("githubOauthToken"));

        RepositoryService service = new RepositoryService();
        List<String> repos = new ArrayList<>();
        for (Repository repo : service.getRepositories("jjYBdx4IL")) {
            repos.add(repo.getName());
            LOG.info(repo.getName() + ": " + repo.getCloneUrl() + (repo.isFork() ? " (forked)" : ""));
        }

        LOG.info("repo count: " + repos.size());
    }
}
