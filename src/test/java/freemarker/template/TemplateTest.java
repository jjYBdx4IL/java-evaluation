/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package freemarker.template;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class TemplateTest {

    private final static Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
    private final static Map<String, Object> root = new HashMap<>();

    @BeforeClass
    public static void beforeClass() {
        //cfg.setDirectoryForTemplateLoading(new File("/where/you/store/templates"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        /* Create a data-model */
        root.put("user", "Big Joe");
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("one", "1a");
        map.put("two", "2a");
        list.add(map);
        map = new HashMap<>();
        map.put("one", "1b");
        map.put("two", "2b");
        list.add(map);
        root.put("list", list);
    }

    private String execute(String tplCode) throws TemplateException, IOException {
        /* Get the template (uses cache internally) */
        Template tpl = new Template("tpl1", tplCode, cfg);
        //Template temp = cfg.getTemplate("...");

        /* Merge data-model with template */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(baos);
        tpl.process(root, out);

        return baos.toString("UTF-8");
    }

    @Test
    public void testPositive() throws IOException, TemplateException {
        assertEquals("Big Joe", execute("${user}"));
    }

    @Test
    public void testMissingData() throws IOException, TemplateException {
        try {
            execute("${notexisting}");
            fail();
        } catch (TemplateException ex) {}
    }

    @Test
    public void testList() throws IOException, TemplateException {
        assertEquals("012", execute("<#list ['a', 'b', 'c'] as x>${x?index}</#list>"));
        assertEquals("01", execute("<#list list as x>${x?index}</#list>"));
        assertEquals("1a/2a1b/2b", execute("<#list list as x>${x.one}/${x.two}</#list>"));
    }
}
