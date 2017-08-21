package tests.javax.ws.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class MyRequestFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(MyRequestFilter.class);
    
    @Override
    public void filter(ContainerRequestContext requestContext)
        throws IOException {

        LOG.info("" + requestContext.getUriInfo().getMatchedResources());

//        final SecurityContext securityContext = requestContext.getSecurityContext();
//        if (securityContext == null ||
//            !securityContext.isUserInRole("privileged")) {
//
//            requestContext.abortWith(Response
//                .status(Response.Status.UNAUTHORIZED)
//                .entity("User cannot access the resource.")
//                .build());
//        }
    }
}