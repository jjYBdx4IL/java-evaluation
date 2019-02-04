package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class SysPropsTest {
    
    @Test
    public void test() {
        final String propname = "de.mGithub jjYBdx4IL Projects.test.set.prop.to.null";
        System.setProperty(propname, "123");
        try {
            System.setProperty(propname, null);
            fail();
        } catch(Exception e) {}
        assertEquals("123", System.getProperty(propname));
        System.clearProperty(propname);
        assertNull(System.getProperty(propname));
    }
    
    @Test
    public void testListProperties() {
        Properties p = System.getProperties();
        p.list(System.out);
    }
}
