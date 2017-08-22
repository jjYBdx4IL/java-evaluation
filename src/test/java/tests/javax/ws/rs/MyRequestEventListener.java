package tests.javax.ws.rs;

import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

// for this to work, we need to supply it to Jersey via the ApplicationEventListener implemented
// in MyApplicationEventListener
public class MyRequestEventListener implements RequestEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(MyRequestEventListener.class);

    private final int requestNumber;
    private final long startTime;

    public MyRequestEventListener(int requestNumber) {
        this.requestNumber = requestNumber;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onEvent(RequestEvent event) {
        LOG.info(event.getType() + " " + event);
        switch (event.getType()) {
            case RESOURCE_METHOD_START:
                LOG.info("Resource method "
                    + event.getUriInfo().getMatchedResourceMethod()
                        .getHttpMethod()
                    + " started for request " + requestNumber);
                LOG.info("resource instance: " + event.getUriInfo().getMatchedResources());
                Method method = event.getUriInfo().getMatchedResourceMethod().getInvocable().getDefinitionMethod();
                LOG.info("method: " + method);
                for (Annotation a : method.getAnnotations()) {
                    LOG.info("anno: " + a);
                }
                break;
            case FINISHED:
                LOG.info("Request " + requestNumber
                    + " finished. Processing time "
                    + (System.currentTimeMillis() - startTime) + " ms.");
                // this can be used for transaction handling:
                LOG.info("exception thrown: " + event.getException());
                LOG.info("" + event.getUriInfo().getMatchedResources());
                break;
            default:
                break;
        }
    }
}