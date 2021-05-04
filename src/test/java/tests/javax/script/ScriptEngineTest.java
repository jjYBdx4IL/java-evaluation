package tests.javax.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNotNull;

import org.junit.Test;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author jjYBdx4IL
 */
public class ScriptEngineTest {

    @Test
    public void test() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        // https://openjdk.java.net/jeps/372
        assumeNotNull(engine);
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
        // https://openjdk.java.net/jeps/372
        assumeNotNull(engine);
        Bindings bindings = engine.createBindings();

        engine.eval("var example = \"abc\";", bindings);

        assertEquals("abc", bindings.get("example"));
    }

}
