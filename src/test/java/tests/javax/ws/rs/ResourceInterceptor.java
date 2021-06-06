package tests.javax.ws.rs;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.core.Response;

/**
 * 
 * from:
 * https://github.com/mgajdos/jersey-intercepting-resource-methods/blob/master/src/main/java/sk/dejavu/blog/examples/intercepting/intercept/MyInterceptionService.java#L23
 */
@SuppressWarnings("resource")
public class ResourceInterceptor implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceInterceptor.class);

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        LOG.info("" + methodInvocation);

        @SuppressWarnings("unused")
        EntityManager em = null; // get entity manager
        try {
            // methodInvocation.getThis() to inject entity manager

            // start transaction
            // EntityTransaction tx = em.getTransaction();
            // tx.begin();

            // force read-only if annotation is found
            // if
            // (methodInvocation.getMethod().isAnnotationPresent(ReadOnly.class))
            // {
            // tx.setRollbackOnly();
            // }

            // returning from this block without committing will force a
            // rollback

            Object ret = null;
            try {
                ret = methodInvocation.proceed();
            } catch (Throwable t) {
                throw t;
            }
            if (ret == null) {
                return null;
            }
            if (ret instanceof Response) {
                Response r = (Response) ret;
                switch (r.getStatusInfo().getFamily()) {
                    case SUCCESSFUL:
                    case INFORMATIONAL:
                        break;
                    default:
                        return ret;
                }
            }
            // commit
            return ret;
        } finally {
            // closeEm(em);
        }
    }

    protected void closeEm(EntityManager em) {
        try {
            EntityTransaction tx = em.getTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            em.close();
        }
    }
}