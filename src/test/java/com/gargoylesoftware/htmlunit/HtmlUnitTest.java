package com.gargoylesoftware.htmlunit;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 *
 * @author jjYBdx4IL
 */
public class HtmlUnitTest extends AbstractHandler {

	public static final String JQUERY_WEBJAR_LOC = "/META-INF/resources/webjars/jquery/3.2.0/jquery.js";

	private static final Logger LOG = LoggerFactory.getLogger(HtmlUnitTest.class);
	private Server server = null;

	@Before
	public void before() throws Exception {
		server = new Server(0);
		server.setHandler(this);
		server.start();
	}

	@After
	public void after() throws Exception {
		server.stop();
	}

	@Test
	public void test() throws Exception {
		try (final WebClient webClient = new WebClient()) {
			final HtmlPage page = webClient.getPage(getUrl("/one"));
			Assert.assertEquals("title", page.getTitleText());

			final String pageAsXml = page.asXml();
			Assert.assertTrue(pageAsXml.contains("<title>"));

			final String pageAsText = page.asText();
			Assert.assertTrue(pageAsText.contains(""));
		}
	}

	@Test
	public void testWithJavascript() throws Exception {
		try (final WebClient webClient = new WebClient()) {
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(true);

			final HtmlPage page = webClient.getPage(getUrl("/two"));
			Assert.assertEquals("", page.getTitleText());

			final String pageAsXml = page.asXml();
			Assert.assertTrue(pageAsXml.contains("<script>"));

			final String pageAsText = page.asText();
			Assert.assertEquals("inserted by js: Next Step...", pageAsText);
		}
	}

	public URL getUrl(String path) throws MalformedURLException, UnknownHostException {
		ServerConnector connector = (ServerConnector) server.getConnectors()[0];
		InetAddress addr = InetAddress.getLocalHost();
		return new URL(String.format(Locale.ROOT, "%s://%s:%d%s", "http", addr.getHostAddress(),
				connector.getLocalPort(), path));
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

		if ("/one".equals(target)) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			response.getWriter().print("<html><title>title</title></html>");
		} else if ("/two".equals(target)) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			response.getWriter()
					.print("<html>\n" + "<head><script src=\"/jquery.js\"></script></head>\n"
							+ "<body onload=\"myFunction()\">\n" + "<p id=\"myText\">inserted by js: </p>\n"
							+ "<script>\n" + "function myFunction() {\n"
							+ "    $( \"#myText\" ).html( $( \"#myText\" ).html() + \"Next Step...\" );\n" + "}\n"
							+ "</script></html>");
		} else if ("/jquery.js".equals(target)) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/javascript");
			try (InputStream is = HtmlUnitTest.class.getResourceAsStream(JQUERY_WEBJAR_LOC)) {
				IOUtils.copy(is, response.getWriter(), "UTF-8");
			}
		}

		baseRequest.setHandled(true);
	}

}
