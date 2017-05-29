package tests.java.util;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing XML properties stuff.
 */
public class PropertiesTest {
    
    @Test
    public void testXmlProperties() throws IOException {
        Properties p = new Properties();
        p.setProperty("key", "value");
        p.setProperty("cat.key", "value2");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        p.storeToXML(os, "some comment", "UTF-8");
        System.out.println(os.toString(Charset.forName("UTF-8").name()));

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        Properties p2 = new Properties();
        p2.loadFromXML(is);
        assertEquals("value", p2.getProperty("key"));
        assertEquals("value2", p2.getProperty("cat.key"));
    }
}
