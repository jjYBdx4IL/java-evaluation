package org.junit;

import com.github.jjYBdx4IL.utils.env.JavaProcess;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class MavenSurefireInspectTest {

    private final static Logger log = Logger.getLogger(MavenSurefireInspectTest.class.getName());
    private final static String PROPNAME_DUMPFILE = MavenSurefireInspectTest.class.getName() + ".dumpfile";

    @Test
    public void buildJUnitRunCommand() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(JavaProcess.getJavaHome()).append(File.separator).append("bin").append(File.separator).append("java");
        sb.append(" -cp ");
        sb.append(JavaProcess.getJavaHome().replace(" ", "\\ "));
        sb.append(" ");
        for (String propName : System.getProperties().stringPropertyNames()) {
            if (propName.matches("^(awt|java|sun|file|user|os|line|maven|surefire)\\..*$")) {
                continue;
            }
            sb.append("-D").append(propName).append("=").append(System.getProperty(propName).replace(" ", "\\ "));
            sb.append(" ");
        }
        sb.append(org.junit.runner.JUnitCore.class.getName());
        sb.append(" ");
        log.debug(sb.toString());
        
        String dumpFile = System.getProperty(PROPNAME_DUMPFILE);
        if (dumpFile == null) {
                return;
        }
        try (OutputStream os = new FileOutputStream(dumpFile)) {
            IOUtils.write(sb.toString(), os, "UTF-8");
        }
    }
}
