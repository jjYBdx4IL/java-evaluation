package tests.javax.ws.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/customResponseType")
public class CustomTypeServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(CustomTypeServiceImpl.class);

    public CustomTypeServiceImpl() {
        LOG.info("init");
    }

    @Context
    private ServletContext context;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CustomType customResponseType(
        @DefaultValue("false") @QueryParam("fail") boolean fail) {

        LOG.info("customResponseType() " + fail);
        LOG.info("servlet context = " + context);

        if (fail) {
            throw new RuntimeException();
        }

        return new CustomType();
    }

}
