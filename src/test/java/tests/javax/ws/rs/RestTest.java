package tests.javax.ws.rs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.net.NetIoUtils;
import com.google.gson.Gson;

public class RestTest extends RestTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(RestTest.class);
    private static final java.util.logging.Logger LOGJ = java.util.logging.Logger.getLogger(RestTest.class.getName());

    @Test
    public void testHello() throws Exception {
        URL url = getServer().getURL("hello");
        LOG.info("server URL: " + url);
        String pageContents = IOUtils.toString(url, "ASCII");
        LOG.info("test page contents: " + pageContents);
        assertEquals("<html> <title>Hello Jersey</title><body><h1>Hello Jersey</body></h1></html> ", pageContents);
        assertEquals("Hello Jersey", NetIoUtils.toString(url, "text/plain; charset=ASCII"));
    }

    @Test
    public void testCustomResponseType() throws Exception {
        URL url = getServer().getURL("customResponseType");
        String pageContents = NetIoUtils.toString(url, "text/plain; charset=ASCII");
        assertEquals("CustomType serialized", pageContents);
    }

    @Test
    public void testParamAndExceptionHandling() throws Exception {
        URL url = new URL(getServer().getURL("customResponseType").toExternalForm() + "?fail=true");
        try {
        	NetIoUtils.toString(url, "text/plain; charset=ASCII");
            fail();
        } catch (IOException ex) {
        }
    }

    @Test
    public void testStorage() throws MalformedURLException, UnknownHostException {
        String url = getServer().getURL("storage").toExternalForm() + "/";
        Client client = JerseyClientBuilder.createClient(new ClientConfig());
        LOGJ.setLevel(java.util.logging.Level.FINEST);
        LOGJ.addHandler(new Handler() {

            @Override
            public void publish(LogRecord record) {
                LOG.info(record.getSourceClassName() + System.lineSeparator() + record.getMessage());

            }

            @Override
            public void flush() {
                // TODO Auto-generated method stub

            }

            @Override
            public void close() throws SecurityException {
                // TODO Auto-generated method stub

            }
        });
        LOGJ.log(java.util.logging.Level.FINEST, "test");
        client.register(new LoggingFeature(LOGJ, LoggingFeature.Verbosity.PAYLOAD_ANY));
        WebTarget webTarget = client.target(url + "1");

        // GET non-existing element
        Response response = (Response) webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals("key not found", response.readEntity(String.class));

        // create the element using PUT
        response = (Response) webTarget.request().put(Entity.entity("content", MediaType.TEXT_PLAIN));
        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

        // GET existing element
        response = (Response) webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("content", response.readEntity(String.class));

        // POST append data to the element
        response = (Response) webTarget.request()
            .post(Entity.entity("payload=more", MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

        // GET appended element
        response = (Response) webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("contentmore", response.readEntity(String.class));

        // POST append data to the element via JSON
        DTO dto = new DTO();
        dto.setaString("123");
        response = (Response) webTarget.request()
            .post(Entity.entity(new Gson().toJson(dto), MediaType.APPLICATION_JSON));
        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

        // GET appended element
        response = (Response) webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("contentmore123", response.readEntity(String.class));

        // GET appended element as JSON
        response = (Response) webTarget.request(MediaType.APPLICATION_JSON).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("contentmore123", response.readEntity(DTO.class).getaString());
        
        // DELETE the element
        response = (Response) webTarget.request(MediaType.TEXT_PLAIN_TYPE).delete();
        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

        // GET non-existing element
        response = (Response) webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals("key not found", response.readEntity(String.class));
    }

}
