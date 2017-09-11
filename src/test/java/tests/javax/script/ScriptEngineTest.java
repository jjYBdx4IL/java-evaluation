package tests.javax.script;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.cache.SimpleDiskCacheEntry;
import com.github.jjYBdx4IL.utils.cache.SimpleDiskCacheEntry.UpdateMode;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author jjYBdx4IL
 */
public class ScriptEngineTest {

    @Test
    public void test() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        Bindings bindings = engine.createBindings();
        bindings.put("example", "123/456/789");

        Object result = engine.eval(
            "var obj = example.split(\"/\"); print(obj[0]); obj[0];",
            bindings);

        assertEquals("123", result);
    }

    @Test
    public void testBindings() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        Bindings bindings = engine.createBindings();

        engine.eval("var example = \"abc\";", bindings);

        assertEquals("abc", bindings.get("example"));
    }

    // run markdown interpreter
    @Test
    public void testShowdown() throws Exception {
        SimpleDiskCacheEntry sde = new SimpleDiskCacheEntry(
            "https://cdn.rawgit.com/showdownjs/showdown/1.7.4/dist/showdown.min.js",
            UpdateMode.NEVER);
        String script;
        try (InputStream is = sde.getInputStream()) {
            script = IOUtils.toString(is, "UTF-8");
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        Bindings bindings = engine.createBindings();
        bindings.put("markdownInput", "#hello, markdown!");

        //engine.eval(script);
        Object result = engine.eval(script + ";" +
            "var converter = new showdown.Converter(),\n" + 
            "    html      = converter.makeHtml(markdownInput); html;",
            bindings);

        assertEquals("<h1 id=\"hellomarkdown\">hello, markdown!</h1>", result);

    }

}
