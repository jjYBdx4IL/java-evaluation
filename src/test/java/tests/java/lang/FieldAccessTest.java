package tests.java.lang;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.klazz.Compile;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FieldAccessTest extends Compile {

    @SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(FieldAccessTest.class.getName());
    private final static File tempDir = Maven.getTempTestDir(FieldAccessTest.class);

    @Before
    public void before() throws IOException {
        // too long commands on Windows...
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
        
        FileUtils.cleanDirectory(tempDir);
        setClassOutputDir(tempDir);
    }

    @Test
    public void testAccessProtectedFieldFromSamePackage() {
        writeClass("pkg", "public class A", "protected String test;");
        writeClass("pkg", "public class B extends A", "public void someMethod() { test = null; }");
        assertCompile();
    }

    @Test
    public void testAccessPkgPrivateFieldFromSamePackage() {
        writeClass("pkg", "public class A", "String test;");
        writeClass("pkg", "public class B extends A", "public void someMethod() { test = null; }");
        assertCompile();
    }

    @Test
    public void testAccessProtectedFieldFromSubPackage() {
        writeClass("pkg", "public class A", "protected String test;");
        writeClass("pkg.sub", new String[]{"pkg.A"}, "public class B extends A", "public void someMethod() { test = null; }");
        assertCompile();
    }

    @Test
    public void testAccessPkgPrivateFieldFromSubPackage() {
        writeClass("pkg", "public class A", "String test;");
        writeClass("pkg.sub", new String[]{"pkg.A"}, "public class B extends A", "public void someMethod() { test = null; }");
        assertNotCompile();
    }
}
