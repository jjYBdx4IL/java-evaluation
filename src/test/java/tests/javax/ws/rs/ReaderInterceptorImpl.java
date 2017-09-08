package tests.javax.ws.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

@Provider
public class ReaderInterceptorImpl implements ReaderInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ReaderInterceptorImpl.class);
    
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        LOG.info(""+context);
        try {
            return context.proceed();
        } catch (Error ex) {
            LOG.info("intercepted error", ex);
            throw ex;
        }
    }

}
