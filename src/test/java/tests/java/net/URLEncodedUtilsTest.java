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
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Evaluation tests for <a
 * href="http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/">org.apache.http.client</a> module.
 *
 * @author Github jjYBdx4IL Projects
 */
public class URLEncodedUtilsTest {

    @Test
    public void testParse() throws URISyntaxException {
        List<NameValuePair> result = URLEncodedUtils.parse(new URI("http://test/?&arg1=value1%3D"),
                "UTF-8");

        assertEquals(1, result.size());
        assertEquals("arg1", result.get(0).getName());
        assertEquals("value1=", result.get(0).getValue());
    }

}
