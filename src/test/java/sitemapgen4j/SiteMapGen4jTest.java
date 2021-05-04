package sitemapgen4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.ibm.icu.util.TimeZone;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testgroup.RequiresIsolatedVM;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Sitemap xml producer for Java.
 * 
 * @author jjYBdx4IL
 */
@Category(RequiresIsolatedVM.class)
public class SiteMapGen4jTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(SiteMapGen4jTest.class);

    @BeforeClass
    public static void beforeClass() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Adding URLs to the sitemap will trigger a flush out to the disk every
     * 50.000 URLs, thereby limiting memory usage when creating sitemaps for very large websites.
     * 
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        String baseUrl = "http://localhost";
        WebSitemapGenerator wsg = WebSitemapGenerator.builder(baseUrl, TEMP_DIR).build();

        final Date now = new Date(1234567890);
        for (int i = 0; i < 50003; i++) {
            wsg.addUrl(new WebSitemapUrl.Options(baseUrl + "/" + i + ".html").lastMod(now).build());
        }

        List<File> files = wsg.write();
        wsg.writeSitemapsWithIndex();

        assertEquals(2, files.size());
        assertTrue(new File(TEMP_DIR, "sitemap_index.xml").exists());
        assertTrue(new File(TEMP_DIR, "sitemap1.xml").exists());
        assertTrue(new File(TEMP_DIR, "sitemap2.xml").exists());

        System.out.println("sitemap_index.xml:");
        System.out.println(FileUtils.readFileToString(new File(TEMP_DIR, "sitemap_index.xml"), "UTF-8"));
        System.out.println("sitemap2.xml:");
        System.out.println(FileUtils.readFileToString(new File(TEMP_DIR, "sitemap2.xml"), "UTF-8"));
    }

    @Test
    public void test2() throws MalformedURLException {
        String baseUrl = "http://localhost";
        WebSitemapGenerator wsg = new WebSitemapGenerator(baseUrl);

        List<String> pages = Arrays.asList(baseUrl + "/1.html", baseUrl + "/2/2.html");
        final Date now = new Date(1234567890);
        pages.forEach(page -> {
            try {
                wsg.addUrl(new WebSitemapUrl.Options(page).lastMod(now).build());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });

        List<String> result = wsg.writeAsStrings();

        assertEquals(1, result.size());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" >\n" +
            "  <url>\n" +
            "    <loc>http://localhost/1.html</loc>\n" +
            "    <lastmod>1970-01-15T06:56:07.890+00:00</lastmod>\n" +
            "  </url>\n" +
            "  <url>\n" +
            "    <loc>http://localhost/2/2.html</loc>\n" +
            "    <lastmod>1970-01-15T06:56:07.890+00:00</lastmod>\n" +
            "  </url>\n" +
            "</urlset>", result.get(0));
    }
}
