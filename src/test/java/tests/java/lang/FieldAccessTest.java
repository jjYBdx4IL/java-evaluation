package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.Compile;
import com.github.jjYBdx4IL.test.FileUtil;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FieldAccessTest extends Compile {

    @SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(FieldAccessTest.class.getName());
    private final static File tempDir = FileUtil.createMavenTestDir(FieldAccessTest.class);

    @Before
    public void before() throws IOException {
        FileUtil.provideCleanDirectory(tempDir);
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
