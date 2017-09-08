package tests.javax.ws.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(HelloServiceImpl.class);

    public HelloServiceImpl() {
        LOG.info("initialized");
    }

    // This method is called if TEXT_PLAIN is request
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Intercept
    public String sayPlainTextHello() {
        LOG.info("sayPlainTextHello()");
        return "Hello Jersey";
    }

    // This method is called if XML is request
    @GET
    @Produces(MediaType.TEXT_XML)
    public String sayXMLHello() {
        LOG.info("sayXMLHello()");
        return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
    }

    // This method is called if HTML is request
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello() {
        LOG.info("sayHtmlHello()");
        return "<html> " + "<title>" + "Hello Jersey" + "</title>" + "<body><h1>" + "Hello Jersey" + "</body></h1>"
            + "</html> ";
    }

}