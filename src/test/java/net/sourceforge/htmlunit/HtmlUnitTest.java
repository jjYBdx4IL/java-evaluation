package net.sourceforge.htmlunit;
/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.gargoylesoftware.css.parser.CSSErrorHandler;
import com.gargoylesoftware.css.parser.CSSException;
import com.gargoylesoftware.css.parser.CSSParseException;
import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.fit.cssbox.CssBoxTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.css.sac.ErrorHandler;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * Small unit test to evaluate the capabilities of
 * <a href="http://htmlunit.sourceforge.net">HtmlUnit</a>.
 *
 * @see <a href="http://htmlunit.sourceforge.net/gettingStarted.html>getting
 *      started</a>
 * @author Github jjYBdx4IL Projects
 */
public class HtmlUnitTest {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlUnitTest.class);
    private Server server = null;
    
    @Before
    public void before() throws Exception {
        server = new Server(0);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(CssBoxTest.getLocalExampleHomepageRoot().getAbsolutePath());
        server.setHandler(resourceHandler);
        server.start();
    }
    
    @After
    public void after() throws Exception {
        server.stop();
    }
    
    public URL getUrl(String path) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(
                String.format(Locale.ROOT, "%s://%s:%d%s", "http", addr.getHostAddress(), connector.getLocalPort(), path));
    }
        
    @Test
    public void homePage() throws Exception {
        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setCssEnabled(true);
            webClient.getOptions().setPrintContentOnFailingStatusCode(true);
            webClient.getOptions().setDownloadImages(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
            webClient.getOptions().setThrowExceptionOnScriptError(true);
            webClient.setAlertHandler(new AlertHandler() {
                
                @Override
                public void handleAlert(Page page, String message) {
                    LOG.error("alert window: " + message);
                }
            });
            webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
                
                @Override
                public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
                    LOG.error("timeoutError()");
                }
                
                @Override
                public void scriptException(HtmlPage page, ScriptException scriptException) {
                    LOG.error("scriptException(): ", scriptException);
                }
                
                @Override
                public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {
                    LOG.error("malformedScriptURL(): ", malformedURLException);
                }
                
                @Override
                public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {
                    LOG.error("loadScriptError(): ", exception);
                }
            });
            webClient.setCssErrorHandler(new CSSErrorHandler() {
                
                @Override
                public void warning(CSSParseException exception) throws CSSException {
                    LOG.warn("CSS warning()", exception);
                }
                
                @Override
                public void fatalError(CSSParseException exception) throws CSSException {
                    LOG.error("CSS fatal error()", exception);
                }
                
                @Override
                public void error(CSSParseException exception) throws CSSException {
                    LOG.error("CSS error()", exception);
                }
            });
            
            final HtmlPage page = webClient.getPage(getUrl("/cssbox_homepage.html"));
            webClient.waitForBackgroundJavaScript(10000L);

            assertEquals("CSSBox - Java HTML rendering engine", page.getTitleText());
            
            final String pageAsXml = page.asXml();
            assertTrue(pageAsXml.contains("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">"));

            final String pageAsText = page.asText();
            assertTrue(pageAsText.contains("Java CSS parser project"));
            
            final String textContent = page.getWebResponse().getContentAsString();
            assertTrue(textContent.contains("<span>Java CSS parser project</span>"));
        }
    }
}
