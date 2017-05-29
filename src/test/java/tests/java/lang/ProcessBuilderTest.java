package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ProcessBuilderTest {

    @SuppressWarnings("unused")
    private final static File TEMP_DIR = FileUtil.createMavenTestDir(ProcessBuilderTest.class);

    @Before
    public void beforeTest() {
        FileUtil.provideCleanDirectory(TEMP_DIR);
    }

    @Test
    public void testStreamInteraction() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("bash");
        File errFile = new File(TEMP_DIR, "stderr");
        File outFile = new File(TEMP_DIR, "stdout");
        pb.redirectError(errFile);
        pb.redirectOutput(outFile);
        Process p = pb.start();
        p.getOutputStream().write("ls pom*.xml\n".getBytes());
        p.getOutputStream().flush();
        p.getOutputStream().write("exit 12\n".getBytes());
        p.getOutputStream().flush();
        p.waitFor();
        assertEquals(12, p.exitValue());
        assertEquals(0, errFile.length());
        assertEquals("pom.xml\n", IOUtils.toString(new FileInputStream(outFile)));
    }
}
