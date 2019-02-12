package linux.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.io.FindUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class RsyncTest {

    private static final File TEST_DIR = Maven.getTempTestDir(RsyncTest.class);
    
    // A very safe and disk-space preserving way to back up a subversion
    // repository (format 3 - each commit adds two files). We use
    // rsync's --backup option to preserve old data from being overwritten
    // in order to protect us from bit rot overwriting perfectly good backup data.
    @Test
    public void testRsyncSvnFormat3Backup() throws InterruptedException, IOException {
        assumeTrue(SystemUtils.IS_OS_UNIX);

        String rsyncCmd = "rsync -irlDc --del"
            + " --backup --backup-dir=../dst.bak/ --suffix=.bak-$(date +%Y%m%d-%H%M%S)"
            + " src/ dst/";
        
        assertEquals(0, runCmd("rsync", "--version"));
        assertEquals(0, runCmd("mkdir", "-p", "src/subdir"));
        
        assertEquals(0, runCmd("bash", "-c", "echo -n 1 > src/subdir/1"));
        assertEquals(0, runCmd("bash", "-c", rsyncCmd));
        assertEquals(0, runCmd("find", ".", "-type", "f"));
        
        System.out.println("## changing the content triggers a backup:");
        assertEquals(0, runCmd("bash", "-c", "echo -n 2 > src/subdir/1"));
        assertEquals(0, runCmd("bash", "-c", "chmod 444 src/subdir/1"));
        assertEquals(0, runCmd("bash", "-c", "touch --date=20010101 src/subdir/1"));
        assertEquals(0, runCmd("bash", "-c", rsyncCmd));
        assertEquals(0, runCmd("find", ".", "-type", "f"));
        File bakFile = FindUtils.globOne(TEST_DIR, "/dst.bak/subdir/1.bak-*");
        assertEquals("1", FileUtils.readFileToString(bakFile,  "UTF-8"));
        
        System.out.println("## neither lmod nor perms trigger a new copy:");
        assertEquals(0, runCmd("bash", "-c", "chmod 755 src/subdir/1"));
        assertEquals(0, runCmd("bash", "-c", "touch src/subdir/1"));
        assertEquals(0, runCmd("bash", "-c", "rm -rf dst.bak"));
        assertEquals(0, runCmd("bash", "-c", rsyncCmd));
        assertEquals(0, runCmd("find", ".", "-type", "f"));
        assertTrue(FindUtils.glob(TEST_DIR, "/dst.bak/subdir/1.bak-*").isEmpty());
        
        System.out.println("## Now, you'd optimally run svnadmin verify on dst/ to verify your data is still sane.");
    }
    
    private int runCmd(String... cmd) throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();
        pb.directory(TEST_DIR);
        pb.environment().put("LC_ALL", "C");
        System.out.println(StringUtils.join(cmd, " "));
        return pb.start().waitFor();
    }
}
