package tests.javax.ws.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
public class WriterInterceptorImpl implements WriterInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(WriterInterceptorImpl.class);
    
    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        LOG.info(""+context);
        try {
            context.proceed();
        } catch (Error ex) {
            LOG.info("intercepted error", ex);
            throw ex;
        }
    }

}
