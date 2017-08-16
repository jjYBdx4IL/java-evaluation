package tests.java.net;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.net.URI;
import java.net.URISyntaxException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
// CHECKSTYLE IGNORE EmptyBlock FOR NEXT 1000 LINES
public class URITest {

    @Test
    public void test() throws URISyntaxException {
        assertEquals("http://host.de", new URI("http", "host.de", null, null, null).toString());
        assertEquals("http://host.de", new URI("http", "host.de", "", null, null).toString());
        assertEquals("http://host.de", new URI("http", "host.de", null, null, null).toString());
        try {
            new URI("http", "host.de", "path", null, null);
            fail();
        } catch (URISyntaxException ex) {
        }
        assertEquals("http://host.de/path", new URI("http", "host.de", "/path", null, null).toString());
        assertEquals("http://host.de?arg", new URI("http", "host.de", null, "arg", null).toString());
        assertEquals("http://host.de?arg=%20", new URI("http", "host.de", null, "arg= ", null).toString());
    }

    @Test
    public void testPathQuoting() throws URISyntaxException {
        assertEquals("http://host/path%20with%20spaces", new URI("http", "host", "/path with spaces", null, null).toString());
        assertEquals("path%20with%20spaces", new URI(null, null, "path with spaces", null, null).toString());
        assertEquals("/path%20with%20spaces", new URI(null, null, "/path with spaces", null, null).toString());
    }

    @Test
    public void testUriSyntaxCheck() {
        assertValidURI("me@localhost");
        assertValidURI("mailto:me@localhost");
        assertValidURI("mailto:me@.com");
    }

    public static void assertValidURI(String url) {
        try {
            new URI(url);
        } catch (URISyntaxException ex) {
            throw new AssertionError(ex);
        }
    }

    public static void assertInvalidURI(String url) {
        try {
            new URI(url);
            fail();
        } catch (URISyntaxException ex) {
        }
    }

}
