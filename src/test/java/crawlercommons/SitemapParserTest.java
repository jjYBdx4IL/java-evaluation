package crawlercommons;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import crawlercommons.filters.basic.BasicURLNormalizer;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.sitemaps.AbstractSiteMap;
import crawlercommons.sitemaps.SiteMap;
import crawlercommons.sitemaps.SiteMapIndex;
import crawlercommons.sitemaps.SiteMapParser;
import crawlercommons.sitemaps.SiteMapURL;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// https://github.com/bithazard/sitemap-parser
// https://github.com/pandzel/RobotsTxt
public class SitemapParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapParserTest.class);

    public static final String TEST_HOST = "https://www.ibm.com/";

    private final Map<String, BaseRobotRules> robots = new HashMap<>();

    @Test
    public void test() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());
        
        List<SiteMap> siteMaps = new ArrayList<>();
        SiteMapParser sitemapParser = new SiteMapParser();
        for (String sitemapUrl : getSitemaps(TEST_HOST)) {
            AbstractSiteMap siteMapCandidate = sitemapParser.parseSiteMap(new URL(sitemapUrl));
            if (siteMapCandidate instanceof SiteMapIndex) {
                SiteMapIndex siteMapIndex = (SiteMapIndex) siteMapCandidate;
                for (AbstractSiteMap aSiteMap : siteMapIndex.getSitemaps()) {
                    if (aSiteMap instanceof SiteMap) {
                        siteMaps.add((SiteMap) aSiteMap);
                    } else {
                        LOG.warn("ignoring site map index inside site map index: " + aSiteMap.getUrl());
                    }
                }
            } else {
                siteMaps.add((SiteMap) siteMapCandidate);
            }
        }
        LOG.info(siteMaps.size() + " site maps found");
        for (SiteMap siteMap : siteMaps) {
            LOG.info("" + siteMap.getUrl());
            for (SiteMapURL url : siteMap.getSiteMapUrls()) {
                LOG.info(siteMap.getUrl() + " -> " + url.getUrl().toExternalForm()
                    + " " + hasAccess(url.getUrl().toExternalForm()));
            }
        }
    }

    public List<String> getSitemaps(String urlString) throws Exception {
        return getRobots(urlString).getSitemaps();
    }

    public boolean hasAccess(String urlString) throws Exception {
        urlString = new BasicURLNormalizer().filter(urlString);
        URL url = new URL(urlString);
        String urlFile = url.getFile();
        return getRobots(urlString).isAllowed(urlFile);
    }

    public BaseRobotRules getRobots(String urlString) throws Exception {
        urlString = new BasicURLNormalizer().filter(urlString);
        URL url = new URL(urlString);
        url = new URL(url.getProtocol().toLowerCase(), url.getHost().toLowerCase(), url.getPort(), "");
        String urlNoPath = url.toExternalForm();
        if (!robots.containsKey(urlNoPath)) {
            try {
                URL robotsTxtUrl = new URL(urlNoPath + "/robots.txt");
                byte[] robotsTxtContent = IOUtils.toByteArray(robotsTxtUrl);
                SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
                BaseRobotRules rules = robotParser.parseContent(
                    robotsTxtUrl.toExternalForm(), robotsTxtContent, "text/plain", "any-darn-crawler");
                robots.put(urlNoPath, rules);
            } catch (FileNotFoundException ex) {
                robots.put(urlNoPath, null);
            }
        }
        return robots.get(urlNoPath);
    }
}
