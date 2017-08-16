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
public class GetClassTest2 extends Compile {

    @SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(GetClassTest2.class.getName());
    private final static File tempDir = FileUtil.createMavenTestDir(GetClassTest2.class);

    @Before
    public void before() throws IOException {
        FileUtil.provideCleanDirectory(tempDir);
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
