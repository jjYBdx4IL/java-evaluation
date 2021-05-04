package com.rometools.rome;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class RomeTest {

    private static final Logger LOG = LoggerFactory.getLogger(RomeTest.class);

    private static final DateFormat DATE_PARSER
            = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void test1() throws IOException, FeedException, ParseException {

        String feedType = "rss_2.0";

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);

        feed.setTitle("Real's HowTo");
        feed.setLink("http://www.rgagnon.com/howto.html");
        feed.setDescription("Useful Java code examples");

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry entry;
        SyndContent description;

        entry = new SyndEntryImpl();
        entry.setTitle("Real's HowTo");
        entry.setLink("http://www.rgagnon.com/java-details/");
        entry.setPublishedDate(DATE_PARSER.parse("2004-06-08"));
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Cool java snippet!");
        entry.setDescription(description);
        entries.add(entry);

        feed.setEntries(entries);

        try (Writer writer = new StringWriter()) {
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer, true);
            LOG.info(writer.toString());
        }
    }

}
