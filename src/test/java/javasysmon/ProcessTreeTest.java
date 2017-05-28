package javasysmon;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.OsProcess;
import com.jezhumble.javasysmon.ProcessInfo;
import java.util.List;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ProcessTreeTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessTreeTest.class);

    @Test
    public void test() {
        JavaSysMon monitor = new JavaSysMon();
        OsProcess proc = monitor.processTree();
        dumpProcessTree(proc, "");
    }
    
    public static void dumpProcessTree(OsProcess proc, String indent) {
        LOG.info(indent + format(proc));
        
        for (OsProcess p : (List<OsProcess>) proc.children()) {
            dumpProcessTree(p, indent + "  ");
        }
    }
    
    private static String format(OsProcess p) {
        if (p.processInfo() == null) {
            return p.toString();
        }
        
        ProcessInfo i = p.processInfo();
        
        StringBuilder sb = new StringBuilder();
        sb.append(i.getPid());
        sb.append(" ");
        sb.append(i.getParentPid());
        sb.append(" ");
        sb.append(i.getName());
        sb.append(" ");
        sb.append(i.getOwner());
        sb.append(" ");
        sb.append(i.getCommand());
        return sb.toString();
    }
}
