package tests.java.util.jar;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import tests.java.io.ResourceTest;

public class JarTest {

    @Test
    public void testListJarResources() throws IOException, URISyntaxException {
        new ResourceTest().testListJarResources();
    }
}
