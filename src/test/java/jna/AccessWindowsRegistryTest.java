package jna;

import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;
import static org.junit.Assume.assumeTrue;

import com.privatejgoodies.common.base.SystemUtils;
import com.sun.jna.platform.win32.Advapi32Util;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessWindowsRegistryTest {

    private static final Logger LOG = LoggerFactory.getLogger(AccessWindowsRegistryTest.class);

    @Before
    public void testBefore() {
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
    }

    @Test
    public void testCheckVirtualBoxInstallation() throws Exception {
        LOG.info("VirtualBox is installed in: {}", Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE,
            "Software\\Oracle\\VirtualBox", "InstallDir"));
    }
}
