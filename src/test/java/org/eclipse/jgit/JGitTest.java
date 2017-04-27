package org.eclipse.jgit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.test.FileUtil;
import com.github.jjYBdx4IL.utils.env.Env;
import com.github.jjYBdx4IL.utils.env.Surefire;
import com.google.common.collect.Lists;

public class JGitTest {

    private static final Logger LOG = LoggerFactory.getLogger(JGitTest.class);
    private static final File TEMP_DIR = FileUtil.createMavenTestDir(JGitTest.class);

    @Before
    public void before() throws IOException {
        FileUtils.cleanDirectory(TEMP_DIR);
    }
    
    @Test
    public void test() throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
        
        // doesn't run from within cygwin bash, jgit computes user home to be C:\cygwin64\Users\<username>
        Assume.assumeFalse(Surefire.isSingleTestExecution());
        
        final File origin = new File(TEMP_DIR, "repo");
        final File clone = new File(TEMP_DIR, "clone");

        // init bare "upstream" repo
        InitCommand initCmd = Git.init();
        initCmd.setDirectory(origin);
        initCmd.setBare(true);
        Git git = initCmd.call();
        assertNotNull(git);

        // clone "upstream" repo
        git = Git.cloneRepository()
                .setURI( origin.toURI().toString() )
                .setDirectory(clone)
                .call();
        assertNotNull(git);
        
        // update remote tracking ref
        StoredConfig config = git.getRepository().getConfig();
        config.setString( ConfigConstants.CONFIG_BRANCH_SECTION, "master", "remote", "origin" );
        config.setString( ConfigConstants.CONFIG_BRANCH_SECTION, "master", "merge", "refs/heads/master" );
        config.save();
        
        //assertTrue(isCleanAndInSync(clone));
        
        // add a few files
        FileUtils.writeStringToFile(new File(clone, "pom.xml"), "content", "ASCII");
        FileUtils.writeStringToFile(new File(clone, "a/b/c/pom.xml"), "content", "ASCII");
        
       // assertFalse(isCleanAndInSync(clone));
        
        AddCommand addCmd = git.add();
        addCmd.addFilepattern(".");
        DirCache dirCache = addCmd.call();
        assertNotNull(dirCache);
        
        //assertFalse(isCleanAndInSync(clone));
        
        // commit
        CommitCommand commitCmd = git.commit().setMessage("Initial commit");
        commitCmd.setMessage("first commit");
        RevCommit revCommit = commitCmd.call();
        assertNotNull(revCommit);

        
//        Repository cloneRepo = new FileRepositoryBuilder().setWorkTree(clone).build();
//
//        RefUpdate branchRefUpdate = cloneRepo.updateRef("refs/heads/master");
//        branchRefUpdate.setNewObjectId(revCommit.getId());
//        branchRefUpdate.update();
//        
//        RefUpdate trackingBranchRefUpdate = cloneRepo.updateRef("refs/remotes/origin/master");
//        trackingBranchRefUpdate.setNewObjectId(revCommit.getId());
//        trackingBranchRefUpdate.update();        
        
        //assertTrue(isCleanAndInSync(clone));
        
        // push; remote master ref not existing before push!
        PushCommand pushCmd = git.push();
        pushCmd.setRemote("origin");
        pushCmd.setRefSpecs(new RefSpec("refs/heads/master:refs/heads/master"));
        //pushCmd.setPushAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pushCmd.setOutputStream(baos);
        //pushCmd.setPushTags();
        List<PushResult> pushResults = Lists.newArrayList(pushCmd.call());
        assertEquals(1, pushResults.size());
        LOG.info(""+pushResults.get(0).getRemoteUpdates().size());
        LOG.info(">"+baos.toString()+"<");
        
        assertTrue(isCleanAndInSync(clone));
        
        CheckoutCommand cmd = git.checkout();
        cmd.setName("master");
        Ref branch = cmd.call();
        assertNotNull(branch);
        
        assertTrue(isCleanAndInSync(clone));
    }
    
    /**
     * no untracked files, nothing uncommitted, HEAD ref same as remote HEAD
     * @throws IOException 
     * @throws GitAPIException 
     * @throws NoWorkTreeException 
     */
    private static boolean isCleanAndInSync(File clone) throws IOException, NoWorkTreeException, GitAPIException {
        Repository cloneRepo = new FileRepositoryBuilder().setWorkTree(clone).build();
        Git git = new Git(cloneRepo);
        
        StatusCommand statusCmd = git.status();
        Status status = statusCmd.call();
        assertNotNull(status);
        
        if (!status.isClean() || !status.getUntrackedFolders().isEmpty()) {
            return false;
        }


        
        DiffCommand diffCmd = git.diff();
        
        ObjectReader reader = git.getRepository().newObjectReader();
        
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        ObjectId oldTree = git.getRepository().resolve( "master^{tree}" );
        assertNotNull(oldTree);
        oldTreeIter.reset( reader, oldTree );
        
        AbstractTreeIterator treeIterator = new FileTreeIterator( git.getRepository() );
        diffCmd.setNewTree(treeIterator);
        diffCmd.setOldTree(oldTreeIter);
        List<DiffEntry> diffEntries = diffCmd.call();
        if (!diffEntries.isEmpty()) {
            return false;
        }
        
        if (!compareRevs(git, "refs/heads/master", "refs/remotes/origin/master")) {
            return false;
        }
        
        return true;
    }
    
    // we are actually comparing against our own, local data here. To compare against remote state, we need to fetch first.
    public static boolean compareRevs(Git git, String rev1, String rev2) throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException, GitAPIException {
        ObjectReader reader = git.getRepository().newObjectReader();
        
        //git.getRepository().exactRef("master").getObjectId();
        
        //git.getRepository().get
        
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        //ObjectId oldTree = git.getRepository().resolve( rev1 );
        Ref head = git.getRepository().exactRef(rev1);
        
        ObjectId oldTree = null;
        try (RevWalk walk = new RevWalk(git.getRepository())) {
            RevCommit commit = walk.parseCommit(head.getObjectId());
            System.out.println("Commit: " + commit);

            // a commit points to a tree
            oldTree = walk.parseTree(commit.getTree().getId());
            //System.out.println("Found Tree: " + tree);

            walk.dispose();
        }        
        
        //ObjectId oldTree = .getObjectId().;
        assertNotNull(oldTree);
        oldTreeIter.reset( reader, oldTree );
        
        Repository rep = git.getRepository();
        LOG.info("remote name: " + rep.getRemoteName(rev2));
        for (String s : rep.getRemoteNames()) {
            LOG.info("remote: " + s);
        }
        for (Entry<String,Ref> entry : rep.getAllRefs().entrySet()) {
            LOG.info("ref: " + entry.getKey() + " -> " + entry.getValue());
        }
        for (Entry<String,Ref> entry : rep.getTags().entrySet()) {
            LOG.info("tag: " + entry.getKey() + " -> " + entry.getValue());
        }
        
        
        head = git.getRepository().exactRef(rev2);
        assertNotNull(head);
        ObjectId newTree = null;
        try (RevWalk walk = new RevWalk(git.getRepository())) {
            RevCommit commit = walk.parseCommit(head.getObjectId());
            System.out.println("Commit: " + commit);

            // a commit points to a tree
            newTree = walk.parseTree(commit.getTree().getId());
            //System.out.println("Found Tree: " + tree);

            walk.dispose();
        }        
        
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        //ObjectId newTree = git.getRepository().resolve( rev2 );
        assertNotNull(newTree);
        newTreeIter.reset( reader, newTree );
        
        DiffCommand diffCmd = git.diff();
        diffCmd.setOldTree(oldTreeIter);
        diffCmd.setNewTree(newTreeIter);
        List<DiffEntry> diffEntries = diffCmd.call();
        return diffEntries.isEmpty();
    }
    
}
