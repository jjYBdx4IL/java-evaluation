/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.javalimits;

import com.github.jjYBdx4IL.test.FileUtil;
import com.github.jjYBdx4IL.utils.ProcRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import org.junit.Before;

/**
*
* @author Github jjYBdx4IL Projects
*/
public class JavaCodeTestBase {
	
    protected static File outDir = null;
    public static final String JAVAC = "javac";
    public static final String OUTPUT_CLASS_NAME = "TestClass";
    public static final String OUTPUT_CLASS_FILE = OUTPUT_CLASS_NAME + ".java";
	protected static String commandOutput;
	protected static int commandExitCode;

    @Before
    public void beforeTest() {
 		outDir = FileUtil.createMavenTestDir(getClass());
        FileUtil.provideCleanDirectory(outDir);
    }

    protected static void assertContains(String part, String text) {
        if (text.contains(part)) {
            return;
        }
        fail("String \"" + part + "\" not found in \"" + text + "\"");
    }

    protected static void testJavac(boolean exitCodeIndicatesSuccess, String expectedConsoleStringPart) throws IOException {
        ProcRunner runner = new ProcRunner(JAVAC, new File(outDir, OUTPUT_CLASS_FILE).getAbsolutePath());
        commandExitCode = runner.run(30000L);
        commandOutput = runner.getOutputBlob();

        if (exitCodeIndicatesSuccess) {
            assertEquals(0, commandExitCode);
        } else {
            assertNotEquals(0, commandExitCode);
        }
        assertContains(expectedConsoleStringPart, commandOutput);
    }

}
