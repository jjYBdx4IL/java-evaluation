package testutils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 *
 * @author jjYBdx4IL
 */
@SuppressWarnings("serial")
public class MethodRef implements Serializable {

    public MethodRef(Class<?> classRef, Method method) {
        this.className = classRef.getName();
        this.methodName = method.getName();
        this.resourceUri = getResourceUri(classRef);
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @return the resourceUri
     */
    public String getResourceUri() {
        return resourceUri;
    }

    private final String className;
    private final String methodName;
    private final String resourceUri;

    public static String getResourceUri(Class<?> classRef) {
        ClassLoader cl = classRef.getClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        String classResourceFileName = classRef.getName().replace('.', '/') + ".class";
        return cl.getResource(classResourceFileName).toString();
    }
    
    /**
     * this returns the value shown in the GUI list:
     */
    @Override
    public String toString() {
        return className + "#" + methodName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.className);
        hash = 73 * hash + Objects.hashCode(this.methodName);
        hash = 73 * hash + Objects.hashCode(this.resourceUri);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MethodRef other = (MethodRef) obj;
        if (!Objects.equals(this.className, other.className)) {
            return false;
        }
        if (!Objects.equals(this.methodName, other.methodName)) {
            return false;
        }
        if (!Objects.equals(this.resourceUri, other.resourceUri)) {
            return false;
        }
        return true;
    }

    
}
