package org.jsoup;

import static com.github.jjYBdx4IL.utils.cache.SimpleDiskCacheEntry.fetch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SelectorTest {

    // https://jsoup.org/apidocs/org/jsoup/select/Selector.html
    @Test
    public void testSelectChildAtTopOnly() {
        String html = "<html><head><title>First parse</title></head>"
            + "<body>\n"
            + "  <table>\n"
            + "    <tr>\n"
            + "      <td>outer1\n"
            + "        <table>\n"
            + "          <tr>\n"
            + "            <td>inner</td>\n"
            + "          </tr>\n"
            + "        </table>\n"
            + "      </td>\n"
            + "      <td>outer2</td>\n"
            + "    </tr>\n"
            + "  </table>\n"
            + "</body></html>";
        Document doc = Jsoup.parse(html);

        Element tr = doc.select("body > table > tbody > tr").get(0);
        // select only the outer tds:
        Elements tds = tr.select(":root > td");
        assertEquals(2, tds.size());
        assertEquals("outer1", tds.get(0).ownText());
        assertTrue(tds.get(0).html().indexOf("inner") > -1);
        assertEquals("outer2", tds.get(1).ownText());
        
        assertEquals(3, doc.select("td").size());
        assertEquals(3, doc.select(":root td").size());
        assertEquals(0, doc.select(":root>td").size());
        assertEquals(3, doc.select("body td").size());
        assertEquals(0, doc.select("body>td").size());
        assertEquals(3, doc.select("body * td").size());
        assertEquals(3, doc.select("tr td").size());
        assertEquals(3, doc.select("table td").size());
        assertEquals(3, doc.select("table * td").size());
    }
    
    @Test
    public void testSelectWl() throws MalformedURLException, IOException {
        Document doc = Jsoup.parse(fetch("https://search.wikileaks.org/gifiles/emailid/273620"));
        System.out.println(doc.select("#doc-description").get(0).wholeText());
    }
}
