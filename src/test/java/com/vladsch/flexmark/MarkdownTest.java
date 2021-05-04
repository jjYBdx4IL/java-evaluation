package com.vladsch.flexmark;

import static org.junit.Assert.assertEquals;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.options.DataHolder;
import org.junit.Test;

public class MarkdownTest {

    static final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(Extensions.ALL & ~Extensions.HARDWRAPS);
    static final Parser PARSER = Parser.builder(OPTIONS).build();
    static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

    @Test
    public void testFrontmatter() {
        String input = "a\nb\n";
        
        Node document = PARSER.parse(input);
        String html = RENDERER.render(document);

        assertEquals("<p>a b</p>", html.trim());
    }
    
}
