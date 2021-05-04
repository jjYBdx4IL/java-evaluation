package org.jsoup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SelectorTest {

    // https://jsoup.org/apidocs/org/jsoup/select/Selector.html
    @Test
    public void testSelectSibling() {
        String html = "<html><head><title>First parse</title></head>"
            + "<body>\n"
            + "  <table>\n"
            + "    <tr>\n"
            + "      <th>key0</th>\n"
            + "      <td>value0</td>\n"
            + "    </tr>\n"
            + "    <tr>\n"
            + "      <th>key</th>\n"
            + "      <td>value</td>\n"
            + "      <td>value2</td>\n"
            + "    </tr>\n"
            + "    <tr>\n"
            + "      <th>key3</th>\n"
            + "      <td>value3</td>\n"
            + "    </tr>\n"
            + "  </table>\n"
            + "</body></html>";
        Document doc = Jsoup.parse(html);

        Elements els = doc.select("th + td");
        assertEquals(3, els.size());
        assertEquals("value0", els.get(0).wholeText());
        assertEquals("value", els.get(1).wholeText());
        assertEquals("value3", els.get(2).wholeText());
        
        els = doc.select("table th:matches(^key$) + td");
        assertEquals(1, els.size());
        assertEquals("value", els.get(0).wholeText());
    }
    
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
    
    // the :last-child selector requires the '>' operator
    @Test
    public void testSelectLastChild() {
        String html = "<html><head><title>First parse</title></head></html>";
        Document doc = Jsoup.parse(html);
        assertEquals(0, doc.select("head:last-child").size());
        Element el = doc.select("head > *:last-child").get(0);
        assertEquals("title", el.tag().getName());
    }
}
