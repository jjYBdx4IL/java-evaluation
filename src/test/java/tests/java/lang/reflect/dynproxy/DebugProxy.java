package tests.java.lang.reflect.dynproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class DebugProxy implements InvocationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DebugProxy.class);
    
    private final Object obj;

    public static Object newInstance(Object obj) {
        return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new DebugProxy(obj));
    }

    private DebugProxy(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable {
        Object result;
        try {
            LOG.info("before method " + m.getName());
            result = m.invoke(obj, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException("unexpected invocation exception: "
                    + e.getMessage());
        } finally {
            LOG.info("after method " + m.getName());
        }
        return result;
    }
}
