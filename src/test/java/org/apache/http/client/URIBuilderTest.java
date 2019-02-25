/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.apache.http.client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class URIBuilderTest {

    @Test
    public void test1() throws URISyntaxException, MalformedURLException {
        URIBuilder b = new URIBuilder();
        b.setHost("host.com");
        b.setScheme("http");
        b.addParameter("a", " ");
        b.addParameter("a", " ");
        assertEquals("http://host.com?a=+&a=+", b.toString());
        assertEquals("http://host.com?a=+&a=+", b.build().toString());
        assertEquals("http://host.com?a=+&a=+", b.build().toURL().toExternalForm());
    }

    @Test
    public void test2() throws URISyntaxException, MalformedURLException {
        URIBuilder b = new URIBuilder("http://host.com?a=+");
        b.addParameter("a", " ");
        assertEquals("http://host.com?a=+&a=+", b.toString());
        assertEquals("http://host.com?a=+&a=+", b.build().toString());
        assertEquals("http://host.com?a=+&a=+", b.build().toURL().toExternalForm());
    }
}
