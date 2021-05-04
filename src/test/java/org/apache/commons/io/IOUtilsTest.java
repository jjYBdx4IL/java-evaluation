package org.apache.commons.io;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class IOUtilsTest {

    @Test
    public void testClasspathJarExtractionByUri() throws IOException, URISyntaxException {
        String result = IOUtils
            .toString(IOUtilsTest.class.getResource("/org/openimaj/text/nlp/warandpeace.txt").toURI(), "UTF-8");
        assertTrue(result.startsWith("The Project Gutenberg EBook of War and Peace, by Leo Tolstoy"));
    }
}
