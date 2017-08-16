/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.javalimits;

import com.github.jjYBdx4IL.utils.env.CI;
import com.github.jjYBdx4IL.utils.env.Surefire;

import com.github.jjYBdx4IL.test.Compile;
import com.github.jjYBdx4IL.test.FileUtil;

import org.apache.log4j.Logger;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class PackageLimitsTest extends Compile {

	private static final Logger log = Logger.getLogger(PackageLimitsTest.class.getName());
	
    @Before
    public void before() {
    	// this test takes too long to be run anywhere else than inside automated CI instances
    	Assume.assumeTrue(CI.isCI() || Surefire.isEclipseDirectSingleJUnit());
    	
    	setClassOutputDir(FileUtil.createMavenTestDir(getClass()));
    }

    @Ignore
    @Test
    public void testPackageClassCountLimit() throws Exception {
    	log.info("output dir: " + getClassOutputDir().getCanonicalPath());
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("public static int main(String[] args) {\n");
    	sb.append("  List<Object> objs = new ArrayList<Object>();\n");
    	
    	for (int i = 0; i <= 500 * 1000; i++) {
    		String className = String.format("MyClass%09d", i);
    		writeClass(
    				"example.pkg",
    				new String[]{},
    				String.format("public class %s", className),
    				""
    				);
    		if (i % 300 == 0) {
    			log.info(className);
    			sb.append(String.format("  objs.add(new %s());\n", className));
    			compile();
    		}
    	}
    	
    	sb.append("  return 0;\n");
    	sb.append("}\n");
    	
    	writeClass("example.pkg", new String[]{"java.util.ArrayList", "java.util.List"}, "public class Main", sb.toString());
    	compile();
    	
    	assertRun("example.pkg.Main");
    }
}
