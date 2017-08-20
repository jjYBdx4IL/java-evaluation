package tests.javax.ws.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/customResponseType")
public class CustomTypeServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(CustomTypeServiceImpl.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CustomType customResponseType() {
        LOG.info("customResponseType()");
        return new CustomType();
    }

}
