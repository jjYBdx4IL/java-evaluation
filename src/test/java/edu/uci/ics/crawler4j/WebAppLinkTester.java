package edu.uci.ics.crawler4j;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class WebAppLinkTester {

    private static final Logger LOG = LoggerFactory.getLogger(WebAppLinkTester.class);
    private int numberOfCrawlers = 1;
    private String crawlStorageFolder = "/tmp/crawl-root";

    public WebAppLinkTester() {
    }

    public void go(String urlString) throws Exception {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxOutgoingLinksToFollow(0);
        LOG.info(config.toString());

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed(urlString);

        controller.start(WebAppLinkTesterCrawler.class, numberOfCrawlers);
    }
}
