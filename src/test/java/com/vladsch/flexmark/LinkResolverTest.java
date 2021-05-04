package com.vladsch.flexmark;

import static org.junit.Assert.assertEquals;

import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.LinkNode;
import com.vladsch.flexmark.ext.wikilink.WikiImage;
import com.vladsch.flexmark.ext.wikilink.WikiLink;
import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.LinkResolverFactory;
import com.vladsch.flexmark.html.renderer.DelegatingNodeRendererFactory;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.html.renderer.LinkStatus;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataHolder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * Based on:
 * https://github.com/vsch/flexmark-java/blob/master/flexmark-java-samples/src/com/vladsch/flexmark/samples/PegdownCustomLinkResolverOptions.java
 * 
 * @author jjYBdx4IL
 */
public class LinkResolverTest {

    private static final Logger LOG = LoggerFactory.getLogger(LinkResolverTest.CustomLinkResolver.class);

    static final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(Extensions.ALL, CustomExtension.create());

    static final Parser PARSER = Parser.builder(OPTIONS).build();
    static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

    static class CustomExtension implements HtmlRenderer.HtmlRendererExtension {
        @Override
        public void rendererOptions(final MutableDataHolder options) {

        }

        @Override
        public void extend(final HtmlRenderer.Builder rendererBuilder, final String rendererType) {
            rendererBuilder.linkResolverFactory(new CustomLinkResolver.Factory());
            rendererBuilder.nodeRendererFactory(new CustomLinkRenderer.Factory());
        }

        static CustomExtension create() {
            return new CustomExtension();
        }
    }

    static class CustomLinkResolver implements LinkResolver {
        public CustomLinkResolver(final LinkResolverContext context) {
            // can use context for custom settings
            // context.getDocument();
            // context.getHtmlOptions();
        }

        @Override
        public ResolvedLink resolveLink(final Node node, final LinkResolverContext context, final ResolvedLink link) {
            // you can also set/clear/modify attributes through
            // ResolvedLink.getAttributes() and
            // ResolvedLink.getNonNullAttributes()

            LOG.info(link.getUrl());
            URI uri = null;
            try {
                uri = new URI(link.getUrl());
                LOG.info("uri absolute: " + uri.isAbsolute());
                LOG.info("uri host: " + uri.getHost());
            } catch (URISyntaxException e) {
                LOG.warn("", e);
            }

            if (node instanceof WikiImage) {
                String url = link.getUrl() + ".png";

                return link.withStatus(LinkStatus.VALID)
                    .withUrl(url);
            } else if (node instanceof WikiLink) {
                String url = link.getUrl() + ".html";

                return link.withStatus(LinkStatus.VALID)
                    .withUrl(url);
            } else if (node instanceof LinkNode) {
                String url = uri.toString();
                if (!uri.isAbsolute() && uri.getRawPath().startsWith("/")) {
                    url = "https://images.localhost" + url;
                } else if (!uri.isAbsolute()) {
                    url = "https://images.localhost/relpath/" + url;
                }

                return link.withStatus(LinkStatus.VALID)
                    .withUrl(url);
            }
            return link;
        }

        static class Factory implements LinkResolverFactory {
            @Override
            public Set<Class<? extends LinkResolverFactory>> getAfterDependents() {
                return null;
            }

            @Override
            public Set<Class<? extends LinkResolverFactory>> getBeforeDependents() {
                return null;
            }

            @Override
            public boolean affectsGlobalScope() {
                return false;
            }

            @Override
            public LinkResolver create(final LinkResolverContext context) {
                return new CustomLinkResolver(context);
            }
        }
    }

    static class CustomLinkRenderer implements NodeRenderer {
        public static class Factory implements DelegatingNodeRendererFactory {
            @Override
            public NodeRenderer create(final DataHolder options) {
                return new CustomLinkRenderer();
            }

            @Override
            public Set<Class<? extends NodeRendererFactory>> getDelegates() {
                return null;
            }
        };

        @Override
        public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
            HashSet<NodeRenderingHandler<?>> set = new HashSet<NodeRenderingHandler<?>>();
            set.add(new NodeRenderingHandler<Link>(Link.class, new CustomNodeRenderer<Link>() {
                @Override
                public void render(Link node, NodeRendererContext context, HtmlWriter html) {
                    if (node.getText().equals("bar")) {
                        html.text("(eliminated)");
                    } else {
                        context.delegateRender();
                    }
                }
            }));
            return set;
        }
    }

    @Test
    public void test() {
        assertEquals("<p>This is <em>Sparta</em> <a href=\"document.html\">document</a> and this is not a link (eliminated)</p>", render("This is *Sparta* [[document]] and this is not a link [bar](/url)"));
        assertEquals("<p><img src=\"https://images.localhost/relpath/a.png\" alt=\"\" /></p>", render("![](a.png)"));
        assertEquals("<p><img src=\"https://images.localhost/a.png\" alt=\"\" /></p>", render("![](/a.png)"));
    }
    
    public static String render(String input) {
        // You can re-use parser and renderer instances
        Node document = PARSER.parse(input);
        String html = RENDERER.render(document);
        LOG.info(html);
        return html.trim();
    }
}
