package com.vladsch.flexmark;

import static org.junit.Assert.assertEquals;

import com.vladsch.flexmark.LinkResolverTest.CustomExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.options.DataHolder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class FrontMatterTest {

    private static final Logger LOG = LoggerFactory.getLogger(FrontMatterTest.class);

    static final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(Extensions.ALL,
        YamlFrontMatterExtension.create(), CustomExtension.create());
    static final Parser PARSER = Parser.builder(OPTIONS).build();
    static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

    @Test
    public void testFrontmatter() {
        String input = "---\n" +
            "title: test\n" +
            "tags:\n" +
            "  - abc\n" +
            "  - def\n" +
            "---\n" +
            "content\n";

        AbstractYamlFrontMatterVisitor visitor = new AbstractYamlFrontMatterVisitor();
        Node document = PARSER.parse(input);
        visitor.visit(document);
        String html = RENDERER.render(document);

        Map<String, List<String>> data = visitor.getData();

        LOG.info(""+data);
        assertEquals(2, data.size());
        assertEquals("[test]", data.get("title").toString());
        assertEquals(2, data.get("tags").size());
        
        assertEquals("<p>content</p>", html.trim());
    }
    
}
