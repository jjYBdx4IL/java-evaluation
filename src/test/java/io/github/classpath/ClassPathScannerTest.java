package io.github.classpath;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import org.apache.commons.lang.SystemUtils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassGraph.ClasspathElementFilter;
import io.github.classgraph.ScanResult;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author jjYBdx4IL
 */
@ExampleAnnotation
public class ClassPathScannerTest {

    @Test
    public void testFindResourcesOnClasspath() {
        Set<String> paths = new HashSet<>();
        try (ScanResult scanResult = new ClassGraph().scan()) {
            scanResult.getResourcesMatchingPattern(Pattern.compile("^org/openimaj/image/.*\\.(png|jpg|jpeg|JPG)$"))
                .forEach(res -> paths.add(res.getPath()));
        }
        assertTrue(paths.contains("org/openimaj/image/data/red-rose.jpeg"));
    }

    @Test
    public void testFindClassesExcludingDependencies() throws MalformedURLException {
        final File moduleClassesDir = new File(System.getProperty("basedir"), "target/test-classes");
        Set<String> klazznames = new HashSet<>();
        ClassLoader cl = new URLClassLoader(new URL[] { moduleClassesDir.toURI().toURL() }, null);
        try (ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .overrideClassLoaders(cl)
            .scan()) {
            scanResult.getAllClasses().forEach(ci -> klazznames.add(ci.getName()));
        }
        assertTrue(klazznames.contains(getClass().getName()));
    }

    @Test
    public void testFindClassesExcludingDependenciesWithoutUsingCL() throws MalformedURLException {
        Set<String> klazznames = new HashSet<>();
        try (ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .scan()) {
            scanResult.getAllClasses()
                .filter(ci -> ci.getResource() != null
                    && ci.getResource().getClasspathElementURI().toString().endsWith("target/test-classes/"))
                .forEach(ci -> klazznames.add(ci.getName()));
        }
        assertTrue(klazznames.contains(getClass().getName()));
    }

    @Test
    public void testFindClassesExcludingDependenciesWithoutUsingCLFast() throws MalformedURLException {
        assumeFalse(SystemUtils.IS_OS_WINDOWS);
        
        ClasspathElementFilter cpef = new ClasspathElementFilter() {
            
            @Override
            public boolean includeClasspathElement(String classpathElementPathStr) {
                return classpathElementPathStr.endsWith("target/test-classes");
            }
        };
        
        Set<String> klazznames = new HashSet<>();
        try (ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .filterClasspathElements(cpef)
            .scan()) {
            scanResult.getAllClasses()
                .forEach(ci -> klazznames.add(ci.getName()));
        }
        assertTrue(klazznames.contains(getClass().getName()));
    }

    @Test
    public void testFindAnnotatedPackages() throws MalformedURLException {
        final File moduleClassesDir = new File(System.getProperty("basedir"), "target/test-classes");
        Set<String> klazznames = new HashSet<>();
        ClassLoader cl = new URLClassLoader(new URL[] { moduleClassesDir.toURI().toURL() }, null);
        try (ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .overrideClassLoaders(cl)
            .scan()) {
            scanResult.getClassesWithAnnotation(ExampleAnnotation.class.getName())
                .forEach(ci -> klazznames.add(ci.getName()));
        }
        assertTrue(klazznames.contains(getClass().getName()));
    }
}
