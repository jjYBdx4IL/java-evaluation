package edu.uci.ics.crawler4j;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class WebAppLinkTesterCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz|xml|rdf|ico))$");

    /**
     * You should implement this function to specify whether the given url should be crawled or not (based on your
     * crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referrringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches(); // && href.startsWith("http://www.ics.uci.edu/");
    }

    /**
     * This function is called when a page is fetched and ready to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        logger.info("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            logger.info("Text length: " + text.length());
            logger.info("Html length: " + html.length());
            logger.info("Number of outgoing links: " + links.size());
        }
    }
    
    
    @Override
	protected void onContentFetchError(WebURL webUrl) {
        logger.info("fetch error: "+webUrl.toString());
        super.onContentFetchError(webUrl);
	}

    @Override
	protected void onParseError(WebURL webUrl) {
        logger.info("parse error: "+webUrl.toString());
        super.onParseError(webUrl);
	}    
}
