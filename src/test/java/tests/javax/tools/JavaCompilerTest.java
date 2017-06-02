package tests.javax.tools;

import com.github.jjYBdx4IL.test.FileUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DirectoryScanner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class JavaCompilerTest {

    private static final Logger LOG = LoggerFactory.getLogger(JavaCompilerTest.class);

    private static final File TEMP_DIR = FileUtil.createMavenTestDir(JavaCompilerTest.class);

    @Before
    public void before() throws IOException {
        FileUtils.cleanDirectory(TEMP_DIR);
    }

    @Test
    public void test() throws Exception {
        FileUtils.write(new File(TEMP_DIR, "Main123.java"), "public class Main123 { public int get() {return 123;}}", "UTF-8");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager jfm = compiler.getStandardFileManager(diagnostics, Locale.ROOT, Charset.forName("UTF-8"));

        File srcDir = TEMP_DIR;
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{"**/*.java"});
        scanner.setBasedir(srcDir);
        scanner.setCaseSensitive(false);
        scanner.scan();
        List<String> srcFileNames = new ArrayList<>();
        for (String s : scanner.getIncludedFiles()) {
            srcFileNames.add(new File(srcDir, s).getAbsolutePath());
        }
        Iterable<? extends JavaFileObject> compilationUnit = jfm.getJavaFileObjects(srcFileNames.toArray(new String[]{}));

        List<String> optionList = new ArrayList<String>();
        optionList.add("-d");
        optionList.add(TEMP_DIR.getAbsolutePath()); // classes destination dir
//        optionList.add("-classpath");
//        optionList.add(System.getProperty("java.class.path") + ";dist/InlineCompiler.jar");

        long duration1 = -System.currentTimeMillis();
        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                jfm,
                diagnostics,
                optionList,
                null,
                compilationUnit);

        assertTrue(task.call());
        duration1 += System.currentTimeMillis();

        for (int i = 0; i < 8; i++) {
            task = compiler.getTask(
                    null,
                    jfm,
                    diagnostics,
                    optionList,
                    null,
                    compilationUnit);

            assertTrue(task.call());
        }

        long duration2 = -System.currentTimeMillis();
        task = compiler.getTask(
                null,
                jfm,
                diagnostics,
                optionList,
                null,
                compilationUnit);

        assertTrue(task.call());
        duration2 += System.currentTimeMillis();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            LOG.error("Error on line {} in {}: {}",
                    diagnostic.getLineNumber(),
                    diagnostic.getSource().toUri(),
                    diagnostic.getMessage(Locale.ROOT));
        }

        jfm.close();

        try {
            Thread.currentThread().getContextClassLoader().loadClass("Main123");
            fail();
        } catch (ClassNotFoundException ex) {
        }

        URLClassLoader classLoader = new URLClassLoader(new URL[]{TEMP_DIR.toURI().toURL()});
        Class<?> loadedClass = classLoader.loadClass("Main123");

        Object o = loadedClass.newInstance();
        Method m = loadedClass.getMethod("get");
        assertEquals(123, m.invoke(o));

        LOG.info("first compilation took {} ms", duration1);
        LOG.info("10th compilation took {} ms", duration2);
    }

}
