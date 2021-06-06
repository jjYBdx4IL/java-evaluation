package io.github.classpath;

import static j2html.TagCreator.filter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassGraph.ClasspathElementFilter;
import io.github.classgraph.ScanResult;

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
    public void testFindExternalClassesOnClasspath() throws MalformedURLException {
        Set<String> klazznames = new HashSet<>();
        final String classname = ScanResult.class.getName();
        try (ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .scan()) {
            scanResult.getAllClasses()
                .filter(ci -> classname.equals(ci.getName()))
                .forEach(ci -> klazznames.add(ci.getName()));
        }
        assertTrue(klazznames.contains(classname));
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
        assertFalse(klazznames.contains(ScanResult.class.getName()));
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

        ClasspathElementFilter cpef = new ClasspathElementFilter() {

            // C:/.../.m2/repository/javax/el/el-api/2.2/el-api-2.2.jar
            // E:/co/.../java-evaluation/target/classes
            // etc.
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
        assertEquals(1, klazznames.size());
    }

    @Test
    public void testFindImplementors() throws MalformedURLException {
        final File moduleClassesDir = new File(System.getProperty("basedir"), "target/test-classes");
        Set<String> klazznames = new HashSet<>();
        ClassLoader cl = new URLClassLoader(new URL[] { moduleClassesDir.toURI().toURL() }, null);
        try (ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .overrideClassLoaders(cl) // this is just to speed things up
            .scan()) {
            scanResult.getClassesImplementing(IExampleIface.class.getName())
                .forEach(ci -> klazznames.add(ci.getName()));
        }
        assertTrue(klazznames.contains(ExampleIfaceImpl.class.getName()));
        assertEquals(1, klazznames.size());
    }
    
    @Test
    public void testFindAnnotatedPackage() throws MalformedURLException, ClassNotFoundException {
        final File moduleClassesDir = new File(System.getProperty("basedir"), "target/test-classes");
        Set<String> klazznames = new HashSet<>();
        ClassLoader cl = new URLClassLoader(new URL[] { moduleClassesDir.toURI().toURL() }, null);
        try (ScanResult scanResult = new ClassGraph()
            .overrideClassLoaders(cl) // this is just to speed things up
            .enableAllInfo()
            .scan()) {
            scanResult.getPackageInfo()
                .filter(pi -> pi.hasAnnotation(ExampleAnnotation.class.getName()))
                .forEach(pi -> klazznames.add(pi.getName()));
        }
        assertEquals(1, klazznames.size());
        String name = klazznames.iterator().next();
        assertEquals(getClass().getPackageName(), klazznames.iterator().next());
        Class<?> kl = Class.forName(name + ".package-info");
        assertNotNull(kl);
    }
}
