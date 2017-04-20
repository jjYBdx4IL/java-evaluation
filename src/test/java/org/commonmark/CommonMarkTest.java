package org.commonmark;

import java.io.IOException;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * See also:
 * https://github.com/atlassian/commonmark-java/blob/master/commonmark/src/test/java/org/commonmark/test/TextContentRendererTest.java
 *
 * @author jjYBdx4IL
 */
public class CommonMarkTest {

    private static final Logger LOG = LoggerFactory.getLogger(CommonMarkTest.class);

    private String toHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String result = renderer.render(document);
        LOG.info(String.format(Locale.ROOT, "%s -> %s", markdown, result));
        return result;
    }

    private String resource2Html(String resourceId) throws IOException {
        String markdown = IOUtils.toString(getClass().getResourceAsStream(resourceId), "UTF-8");
        return toHtml(markdown);
    }

    @Test
    public void testCodeBlock() {
        assertEquals("<pre><code>  1 2   3\n"
                + "1    4\n"
                + "</code></pre>\n", toHtml("```\n  1 2   3\n1    4\n```\n"));
    }

    @Test
    public void testUnorderedLists() {
        assertEquals("<ul>\n"
                + "<li>A</li>\n"
                + "<li>B</li>\n"
                + "</ul>\n", toHtml("* A\n* B\n"));
        assertEquals("<ul>\n"
                + "<li>A</li>\n"
                + "<li>B</li>\n"
                + "</ul>\n", toHtml("- A\n- B\n"));
    }

    @Test
    public void testOrderedLists() {
        assertEquals("<ol>\n"
                + "<li>A</li>\n"
                + "<li>B</li>\n"
                + "</ol>\n", toHtml("1. A\n2. B\n"));
        assertEquals("<ol>\n"
                + "<li>A</li>\n"
                + "<li>B</li>\n"
                + "</ol>\n", toHtml("1) A\n2) B\n"));
    }

    @Test
    public void testImage() {
        assertEquals("<p>foo <img src=\"http://link\" alt=\"\" /> bar</p>\n",
                toHtml("foo ![](http://link) bar"));
        assertEquals("<p>foo <img src=\"http://link\" alt=\"text\" /> bar</p>\n",
                toHtml("foo ![text](http://link) bar"));
    }

    @Test
    public void testLink() {
        assertEquals("<p>foo <a href=\"http://link\">text</a> bar</p>\n",
                toHtml("foo [text](http://link) bar"));
        assertEquals("<p>foo <a href=\"http://link\" title=\"title\">text</a> bar</p>\n",
                toHtml("foo [text](http://link \"title\") bar"));
    }

    @Test
    public void testBold() {
        assertEquals("<p>This is <em>Sparta</em></p>\n", toHtml("This is *Sparta*"));
    }

    @Test
    public void testHeadings() throws IOException {
        assertEquals("<h1>Blog Title</h1>\n"
                + "<h2>Blog Entry 1</h2>\n"
                + "<p>Some example text for the first blog entry.</p>\n"
                + "<h2>Blog Entry 2</h2>\n", resource2Html("test.md"));
    }

    @Test
    public void testVisitor() throws IOException {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(IOUtils.toString(getClass().getResourceAsStream("test.md"), "UTF-8"));
        document.accept(new CommonMarkVisitor());
    }
}
