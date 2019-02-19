/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package freemarker.template;

import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.env.Maven;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Freemarker template examples in Java.
 * 
 * https://freemarker.apache.org/
 *
 * @author Github jjYBdx4IL Projects
 */
public class TemplateTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(TemplateTest.class);
    private static final Configuration CFG = new Configuration(Configuration.VERSION_2_3_28);
    private static final Map<String, Object> ROOT = new HashMap<>();

    @BeforeClass
    public static void beforeClass() {
        CFG.setDefaultEncoding("UTF-8");
        CFG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        /* Create a data-model */
        ROOT.put("user", "Big Joe");
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("one", "1a");
        map.put("two", "2a");
        list.add(map);
        map = new HashMap<>();
        map.put("one", "1b");
        map.put("two", "2b");
        list.add(map);
        ROOT.put("list", list);
    }

    private String execute(String tplCode) throws TemplateException, IOException {
        /* Get the template (uses cache internally) */
        Template tpl = new Template("tpl1", tplCode, CFG);
        // Template temp = cfg.getTemplate("...");

        /* Merge data-model with template */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(baos);
        tpl.process(ROOT, out);

        return baos.toString("UTF-8");
    }

    // example, how to load and parse templates directly from disk
    @Test
    public void testInclude() throws IOException, TemplateException {
        FileUtils.write(new File(TEMP_DIR, "outer.html"), "a<#include \"inner.html\">c", "UTF-8");
        FileUtils.write(new File(TEMP_DIR, "inner.html"), "${user}", "UTF-8");

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setDirectoryForTemplateLoading(TEMP_DIR);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        Template tpl = cfg.getTemplate("outer.html");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tpl.process(ROOT, new OutputStreamWriter(baos));

        assertEquals("aBig Joec", new String(baos.toByteArray(), "UTF-8"));
    }

    @Test
    public void testPositive() throws IOException, TemplateException {
        assertEquals("Big Joe", execute("${user}"));
    }

    @Test(expected = TemplateException.class)
    public void testMissingData() throws IOException, TemplateException {
        execute("${notexisting}");
    }
    
    // assigning variables in templates
    @Test
    public void testAssign() throws TemplateException, IOException {
        assertEquals("Big Joe", execute("<#assign me = user>${me}"));
        assertEquals("cool joe", execute("<#assign me = 'cool joe'>${me}"));
    }

    @Test
    public void testList() throws IOException, TemplateException {
        assertEquals("012", execute("<#list ['a', 'b', 'c'] as x>${x?index}</#list>"));
        assertEquals("01", execute("<#list list as x>${x?index}</#list>"));
        assertEquals("1a/2a1b/2b", execute("<#list list as x>${x.one}/${x.two}</#list>"));
    }
}
