package tests.javax.ws.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class MyResponseFilter implements ContainerResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(MyResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
        throws IOException {

        LOG.info("" + requestContext.getUriInfo().getMatchedResources());

        // responseContext.getHeaders().add("X-Powered-By", "Jersey :-)");
    }
}