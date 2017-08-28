package io.github.lukehutch.fastclasspathscanner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.env.CI;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.github.lukehutch.fastclasspathscanner.matchprocessor.ClassAnnotationMatchProcessor;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchProcessor;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchProcessorWithContext;

/**
 *
 * @author jjYBdx4IL
 */
public class ClassPathScannerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ClassPathScannerTest.class);

    @Before
    public void before() {
        // for some strange reason this test is crashing the JVM inside travis environment
        assumeFalse(CI.isCI());
    }
    
    @Test
    public void testFindResourcesOnClasspath() {
        FileMatchProcessor fileMatchProcessor = new FileMatchProcessor() {
            @Override
            public void processMatch(String relativePath, InputStream inputStream, long lengthBytes) throws IOException {
                LOG.debug(relativePath);
            }
        };

        FastClasspathScanner scanner = new FastClasspathScanner();
        scanner.matchFilenamePattern("^org/openimaj/image/.*\\.(png|jpg|jpeg|JPG)", fileMatchProcessor);
        scanner.scan();
    }

    @Test
    public void testFindClassesExcludingDependencies() {
        final File moduleClassesDir = new File(System.getProperty("basedir"), "target/test-classes");

        FileMatchProcessorWithContext fileMatchProcessorWithContext = new FileMatchProcessorWithContext() {
            @Override
            public void processMatch(File classpathElt, String relativePath, InputStream inputStream, long lengthBytes) throws IOException {
                if (moduleClassesDir.equals(classpathElt)) {
                    LOG.debug("class from test-classes folder: " + relativePath);
                }
            }
        };

        FastClasspathScanner scanner = new FastClasspathScanner();
        scanner.matchFilenamePattern(".*", fileMatchProcessorWithContext);
        scanner.scan();
    }

    @Test
    public void testFindAnnotatedPackages() {
        final List<String> foundClassNames = new ArrayList<>();

        ClassAnnotationMatchProcessor classAnnotationMatchProcessor = new ClassAnnotationMatchProcessor() {
            @Override
            public void processMatch(Class<?> classWithAnnotation) {
                foundClassNames.add(classWithAnnotation.getName().replaceAll("\\.package-info$", ""));
            }
        };

        FastClasspathScanner scanner = new FastClasspathScanner();
        scanner.matchClassesWithAnnotation(ExamplePackageAnnotation.class, classAnnotationMatchProcessor);
        scanner.scan();

        assertArrayEquals(new String[]{getClass().getPackage().getName()}, foundClassNames.toArray());
    }

    @Test
    public void testFindAnnotatedPackagesInCurrentModuleOnly() throws Throwable {
        String moduleUriPrefix = "";//new File(System.getProperty("basedir")).toURI().toString();

        final List<String> foundClassNames = new ArrayList<>();

        ClassAnnotationMatchProcessor classAnnotationMatchProcessor = new ClassAnnotationMatchProcessor() {
            @Override
            public void processMatch(Class<?> classRef) {
                try {
                    String fullResourcePath = getResourceUri(classRef);
                    LOG.info(fullResourcePath);
                    if (fullResourcePath.startsWith(moduleUriPrefix)) {
                        // also test reading anno param
                        assertEquals(123, classRef.getAnnotation(ExamplePackageAnnotation.class).value());
                        foundClassNames.add(classRef.getName().replaceAll("\\.package-info$", ""));
                    }
                } catch (Exception ex) {
                    LOG.error("", ex);
                }
            }
        };

        FastClasspathScanner scanner = new FastClasspathScanner();
        scanner.matchClassesWithAnnotation(ExamplePackageAnnotation.class, classAnnotationMatchProcessor);
        //scanner.verbose(true);
        scanner.scan();

        assertArrayEquals(new String[]{getClass().getPackage().getName()}, foundClassNames.toArray());
    }

    public static String getResourceUri(Class<?> classRef) {
        ClassLoader cl = classRef.getClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        String classResourceFileName = classRef.getName().replace('.', '/') + ".class";
        return cl.getResource(classResourceFileName).toString();
    }
}
