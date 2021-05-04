package org.apache.http.client;

import static org.junit.Assert.assertEquals;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Example on how to decode URL parameters.
 *
 * @author Github jjYBdx4IL Projects
 */
public class URLEncodedUtilsTest {

    @Test
    public void testUrlParameterDecode() throws URISyntaxException {
        List<NameValuePair> result = URLEncodedUtils.parse(new URI("http://test/?&arg1=value1%3D"),
                StandardCharsets.UTF_8);

        assertEquals(1, result.size());
        assertEquals("arg1", result.get(0).getName());
        assertEquals("value1=", result.get(0).getValue());
    }

}
