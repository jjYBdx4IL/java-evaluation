package net.sourceforge.htmlunit;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Small unit test to evaluate the capabilities of
 * <a href="http://htmlunit.sourceforge.net">HtmlUnit</a>.
 *
 * @see <a href="http://htmlunit.sourceforge.net/gettingStarted.html>getting started</a>
 * @author Github jjYBdx4IL Projects
 */
public class HtmlUnitTest {

    @Ignore
    @Test
    public void homePage() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");

        Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());

        final String pageAsXml = page.asXml();
        Assert.assertTrue(pageAsXml.contains("<body class=\"composite\">"));

        final String pageAsText = page.asText();
        Assert.assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));
    }
}
