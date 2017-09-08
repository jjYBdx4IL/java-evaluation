package tests.javax.ws.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
public class MyResponseFilter implements ContainerResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(MyResponseFilter.class);

    // beware of thread safety here! filters are singletons!
    @Context
    private HttpServletRequest httpRequest;
    
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
        throws IOException {

        LOG.info("" + requestContext.getUriInfo().getMatchedResources());
        LOG.info("" + httpRequest);
        LOG.info("statusInfo: " + responseContext.getStatusInfo().getStatusCode());
        LOG.info("statusInfo: " + responseContext.getStatusInfo().getFamily());

        // responseContext.getHeaders().add("X-Powered-By", "Jersey :-)");
    }
}