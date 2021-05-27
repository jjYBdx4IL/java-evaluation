package javacpp.opencv;

import static org.junit.Assert.assertNotNull;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testgroup.RequiresIsolatedVM;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Category(RequiresIsolatedVM.class)
public class LoadOrderDetectorTest {

    private static final Logger LOG = LoggerFactory.getLogger(LoadOrderDetectorTest.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(LoadOrderDetectorTest.class);
    private static final File DEPS_CACHE_FILE = new File(TEMP_DIR.getParentFile(),
        TEMP_DIR.getName() + ".cache.libloaddeps");
    private static final String LIB_EXT = SystemUtils.IS_OS_UNIX ? ".so" : ".dll";
    private static final String LIB_PREFIX = SystemUtils.IS_OS_UNIX ? "lib" : "";

    static {
        System.setProperty("org.bytedeco.javacpp.loadlibraries", "false");
    }

    @Test
    public void test() {
        loadLibs("jniopencv_imgproc", "jniopencv_imgcodecs", "jniopencv_core");

        Mat m = new Mat();
        assertNotNull(m);
    }

    /**
     * @param libNames
     *            a list of lib names, ie. "jniopencv_imgproc", etc.
     *            Dependencies will be loaded automatically, so only the JNI lib
     *            needs to be specified. The lib prefix commonly used on Linux
     *            must be omitted.
     */
    public static void loadLibs(String... libNames) {
        if (libNames.length == 0) {
            throw new RuntimeException("no lib names given");
        }

        if (!LIB_PREFIX.isEmpty()) {
            String[] newArr = new String[libNames.length];
            for (int i = 0; i < newArr.length; i++) {
                newArr[i] = LIB_PREFIX + libNames[i];
            }
            libNames = newArr;
        }

        try {
            long libLoadingTimeMs = -System.currentTimeMillis();

            Map<String, List<String>> deps = null;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (!DEPS_CACHE_FILE.exists()) {
                deps = getDependencies();

                String json = gson.toJson(deps, new TypeToken<Map<String, List<String>>>() {
                }.getType());
                LOG.info("saving library dependencies to: " + DEPS_CACHE_FILE.getAbsolutePath());
                FileUtils.write(DEPS_CACHE_FILE, json, StandardCharsets.UTF_8);
            }

            String json = FileUtils.readFileToString(DEPS_CACHE_FILE, StandardCharsets.UTF_8);
            deps = gson.fromJson(json, new TypeToken<Map<String, List<String>>>() {
            }.getType());

            List<String> primaryLibPaths = resolveLibNames(deps, libNames);
            loadLibs2(deps, primaryLibPaths, null);

            libLoadingTimeMs += System.currentTimeMillis();
            LOG.info(String.format("lib loading took %,d ms", libLoadingTimeMs));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void loadLibs2(Map<String, List<String>> deps, List<String> libPaths, Set<String> loadedLibs) {
        if (loadedLibs == null) {
            loadedLibs = new HashSet<>();
        }

        for (String libPath : libPaths) {
            if (loadedLibs.contains(libPath)) {
                continue;
            }
            loadedLibs.add(libPath);
            loadLibs2(deps, deps.get(libPath), loadedLibs);
            if (LOG.isDebugEnabled()) {
                LOG.debug("loading " + libPath);
            }
            System.load(libPath);
        }
    }
    
    private static List<String> resolveLibNames(Map<String, List<String>> deps, String... libNames) {

        // resolve libNames to libs on the loadOrder list
        Set<String> libNameSet = new HashSet<>();
        for (String libName : libNames) {
            libNameSet.add(libName.toLowerCase(Locale.ROOT));
        }

        Set<String> needed = new HashSet<>();
        for (String libPath : deps.keySet()) {
            String libName = new File(libPath).getName().toLowerCase(Locale.ROOT);
            String match = null;
            for (String libName2 : libNameSet) {
                if (libName.startsWith(libName2 + LIB_EXT)) {
                    if (match != null) {
                        throw new RuntimeException("library not uniquely defined by: " + libName2);
                    }
                    match = libName2;
                }
            }
            if (match != null && libNameSet.remove(match)) {
                LOG.debug("adding primary library to load: " + libPath);
                needed.add(libPath);
            }
        }

        if (!libNameSet.isEmpty()) {
            throw new RuntimeException("failed to resolve lib name: " + libNameSet.iterator().next());
        }
        
        return new ArrayList<>(needed);
    }

    private static Map<String, List<String>> getDependencies() throws IOException {
        File basedir = new File(Maven.getBasedir(LoadOrderDetectorTest.class));
        File relLibDir = new File("target", "natives");
        File libdir = new File(basedir, relLibDir.getPath());
        List<File> libFiles = new ArrayList<>(Arrays.asList(libdir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                String lc = name.toLowerCase(Locale.ROOT);
                return (lc.endsWith(LIB_EXT) || lc.contains(LIB_EXT + "."));
            }
        })));
        
        List<String> libs = new ArrayList<>();
        libFiles.forEach(f -> libs.add(f.getAbsolutePath()));
        
        Map<String, List<String>> deps = new HashMap<>();

        for (String lib : libs) {
            String s = FileUtils.readFileToString(new File(lib), StandardCharsets.US_ASCII).toLowerCase(Locale.ROOT);
            if (LOG.isDebugEnabled()) {
                LOG.debug(lib + ":");
            }
            String n = new File(lib).getName().toLowerCase(Locale.ROOT);
            List<String> deps2 = new ArrayList<>();
            for (String lib2 : libs) {
                String nn = new File(lib2).getName().toLowerCase(Locale.ROOT);
                if (!n.equals(nn) && s.contains(nn)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("    " + nn);
                    }
                    deps2.add(lib2);
                }
            }
            deps.put(lib, deps2);
        }

        return deps;
    }
}
