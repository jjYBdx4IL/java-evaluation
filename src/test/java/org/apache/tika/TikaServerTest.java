package org.apache.tika;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.net.PortUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class TikaServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(TikaServerTest.class);
    
    public static final int TIKA_SERVER_PORT = 9998;
    public static final String TIKA_SERVER_URL = "http://localhost:" + TIKA_SERVER_PORT;

    @Before
    public void before() {
        assumeTrue("tika server not running on port " + TIKA_SERVER_PORT, PortUtils.isOpen(9998));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void test() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(TIKA_SERVER_URL + "/tika");
            httpPut.setEntity(new InputStreamEntity(new ByteArrayInputStream(ConversionTest.HTML_EXAMPLE.getBytes())));
            httpPut.setHeader("Content-Type", "text/html");
            httpPut.setHeader("Accept", "text/plain");
            httpPut.setHeader("Accept-Charset", "ISO-8859-1");
            try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
                assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
                assertEquals("text/plain", response.getEntity().getContentType().getValue());
                assertEquals("text/plain", response.getFirstHeader("Content-Type").getValue());
                // replies are seemingly always UTF-encoded
                assertEquals("\n" +
                    "This is a very simple HTML document\n" +
                    "\n" +
                    "It only has two paragraphs ö\n" +
                    "\n", IOUtils.toString(response.getEntity().getContent(), "UTF-8").replace("\r", ""));
            }
            httpPut = new HttpPut(TIKA_SERVER_URL + "/meta");
            httpPut.setEntity(new InputStreamEntity(new ByteArrayInputStream(ConversionTest.HTML_EXAMPLE.getBytes())));
            httpPut.setHeader("Content-Type", "text/html");
            httpPut.setHeader("Accept", "application/json;charset=UTF-8");
            try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
                assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
                try (InputStream is = response.getEntity().getContent()) {
                    try (InputStreamReader reader = new InputStreamReader(is)) {
                        JsonParser parser = new JsonParser();
                        JsonElement root = parser.parse(reader);
                        LOG.info(root.toString());
                        MetaReply reply = new Gson().fromJson(root, MetaReply.class);
                        assertNotNull(reply);
                        assertEquals("text/html; charset=ISO-8859-1", reply.contenttype);
                        assertEquals("abc,def", reply.keywords);
                        assertEquals("org.apache.tika.parser.html.HtmlParser", reply.parsedBy.get(1));
                        assertEquals("A Simple HTML Document", reply.title);
                    }
                }
            }
        }

    }

    public static class MetaReply {
        @SerializedName("Content-Type")
        public String contenttype;
        @SerializedName("Content-Encoding")
        public String contentEncoding;
        public String keywords;
        @SerializedName("X-Parsed-By")
        public List<String> parsedBy;
        public String title;
        public String language;
        public String robots;
    }
}
