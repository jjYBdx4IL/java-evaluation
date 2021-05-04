package tests.java.lang;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.klazz.Compile;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class GetClassTest2 extends Compile {

    @SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(GetClassTest2.class.getName());
    private final static File tempDir = Maven.getTempTestDir(GetClassTest2.class);

    @Before
    public void before() throws IOException {
        FileUtils.cleanDirectory(tempDir);
        setClassOutputDir(tempDir);
    }

    @Test
    public void testGetClassFromParentClass() {
        writeClass("pkg", "public class A", "public String ret() {return getClass().getName();}");
        writeClass("pkg", new String[]{"static org.junit.Assert.*"}, "public class B extends A",
                "public void test() {assertEquals(\"pkg.B\", super.ret());}"
                + "public static void main(String[] args) {new B().test();}");
        assertCompile();
        assertRun("pkg.B");
    }
}
