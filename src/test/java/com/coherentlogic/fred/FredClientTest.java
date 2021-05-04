/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2015 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package com.coherentlogic.fred;

import static com.coherentlogic.coherent.data.model.core.util.Utils.using;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.coherentlogic.coherent.data.model.core.cache.CacheServiceProviderSpecification;
import com.coherentlogic.coherent.data.model.core.cache.MapCompliantCacheServiceProvider;
import com.coherentlogic.fred.client.core.builders.QueryBuilder;
import com.coherentlogic.fred.client.core.domain.Categories;
import com.coherentlogic.fred.client.core.domain.Category;
import com.coherentlogic.fred.client.core.domain.FileType;
import com.coherentlogic.fred.client.core.domain.Frequency;
import com.coherentlogic.fred.client.core.domain.Message;
import com.coherentlogic.fred.client.core.domain.Observation;
import com.coherentlogic.fred.client.core.domain.Observations;
import com.coherentlogic.fred.client.core.domain.OrderBy;
import com.coherentlogic.fred.client.core.domain.OutputType;
import com.coherentlogic.fred.client.core.domain.SearchType;
import com.coherentlogic.fred.client.core.domain.Series;
import com.coherentlogic.fred.client.core.domain.Seriess;
import com.coherentlogic.fred.client.core.domain.SortOrder;
import com.coherentlogic.fred.client.core.domain.Unit;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import com.github.jjYBdx4IL.utils.junit4.Screenshot;
import org.apache.commons.io.IOUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Based on:
 * http://sourceforge.net/p/clfredclient/code/259/tree/trunk/fred-client-core-it/src/test/java/com/coherentlogic/fred/client/core/builders/QueryBuilderTest.java
 *
 * @author Github jjYBdx4IL Projects
 */
// @disable:output@
@SuppressWarnings("unused")
public class FredClientTest extends InteractiveTestBase {

    private static final File TEMP_DIR = Maven.getTempTestDir(FredClientTest.class);
    private static final Logger LOG = LoggerFactory.getLogger(FredClientTest.class);
    public final static String FRED_REST_TEMPLATE_ID = "fredRestTemplate";
    private final static String API_KEY;
    private final static Date REALTIME_START = using(2001, Calendar.JANUARY, 20);
    private final static Date REALTIME_END = using(2004, Calendar.MAY, 17);

    static {
        try {
            File f = new File(Maven.getMavenBuildDir(FredClientTest.class).getParentFile(), "fredapi.key");
            API_KEY = IOUtils.toString(f.toURI(), "UTF-8").trim();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    private final ApplicationContext context = new FileSystemXmlApplicationContext(
        "src/test/resources/com/coherentlogic/fred/application-context.xml");

    private RestTemplate restTemplate = null;

    @Before
    public void setUp() throws Exception {
        restTemplate = (RestTemplate) context.getBean(FRED_REST_TEMPLATE_ID);
    }

    @After
    public void tearDown() throws Exception {
        restTemplate = null;
    }

    @Test
    public void getSeries() {
        assertNotNull(restTemplate);

        QueryBuilder builder = new QueryBuilder(restTemplate);

        Seriess result = builder
            .series()
            .setApiKey(API_KEY)
            .setSeriesId("GNPCA")
            .setRealtimeStart(REALTIME_START)
            .setRealtimeEnd(REALTIME_END)
            .doGet(Seriess.class);

        Date realtimeStartDate = result.getRealtimeStart();
        Date realtimeEndDate = result.getRealtimeEnd();

        assertNotNull("realtimeStart", realtimeStartDate);

        List<Series> seriesList = result.getSeriesList();

        Series seriesOne = seriesList.get(1);

        assertEquals("GNPCA", seriesOne.getId());
        assertEquals("Real Gross National Product", seriesOne.getTitle());

        assertEquals(Frequency.a, seriesOne.getFrequency());
        assertEquals("Billions of Chained 2000 Dollars", seriesOne.getUnits());
        assertEquals("Bil. of Chn. 2000 $", seriesOne.getUnitsShort());
        assertEquals(
            "Not Seasonally Adjusted", seriesOne.getSeasonalAdjustment());
        assertEquals("NSA", seriesOne.getSeasonalAdjustmentShort());
        // Popularity may change so we'll just check for null.
        assertNotNull(seriesOne.getPopularity());
    }

    @Test
    public void getSeriesCategories() {

        QueryBuilder builder = new QueryBuilder(restTemplate);

        Categories categories = builder
            .series()
            .categories()
            .setApiKey(API_KEY)
            .setSeriesId("EXJPUS")
            .setRealtimeStart(REALTIME_START)
            .setRealtimeEnd(REALTIME_END)
            .doGet(Categories.class);

        List<Category> categoryList = categories.getCategoryList();

        assertEquals(2, categoryList.size());

        Category cat275 = categoryList.get(1);

        assertEquals("275", cat275.getId());
        assertEquals("Japan", cat275.getName());
        assertEquals("158", cat275.getParentId());
    }

    @Test
    public void getSeriesObservationsExpectingXML() {

        QueryBuilder builder = new QueryBuilder(restTemplate);

        Observations observations = builder
            .series()
            .observations()
            .setApiKey(API_KEY)
            .setSeriesId("GNPA")
            .doGet(Observations.class);

        Message content = observations.getMessage();

        assertNull(content);

        assertEquals(Unit.lin, observations.getUnits());
        assertEquals(
            OutputType.observationsByRealTimePeriod,
            observations.getOutputType());
        assertEquals(
            FileType.xml,
            observations.getFileType());
        assertEquals(SortOrder.asc, observations.getSortOrder());
        assertTrue(observations.getCount() >= 87);

        List<Observation> observationList = observations.getObservationList();

        Observation obs2 = observationList.get(2);

        assertEquals(new BigDecimal("77.906"), obs2.getValue());
    }

    @Test
    public void getSeriesObservationsExpectingXMLAndUsingACache() {

        final AtomicInteger updateCount = new AtomicInteger(0);

        File dbFile = new File(TEMP_DIR, "db");

        DB db = DBMaker
            .fileDB(dbFile)
            .closeOnJvmShutdown()
            .make();
        @SuppressWarnings("unchecked")
        final HTreeMap<String, Object> cacheMap = (HTreeMap<String, Object>) db.hashMap("map").createOrOpen();
        CacheServiceProviderSpecification<String, Object> cacheProvider = new MapCompliantCacheServiceProvider<>(
            cacheMap);

        QueryBuilder builder = new QueryBuilder(restTemplate, cacheProvider);

        assertEquals(0, updateCount.get());

        Observations observations = builder
            .series()
            .observations()
            .setApiKey(API_KEY)
            .setSeriesId("GNPA")
            .doGet(Observations.class);

        // assertEquals(1, updateCount.get());

        assertTrue(observations.getCount() >= 87);

        List<Observation> observationList = observations.getObservationList();

        Observation obs2 = observationList.get(2);

        assertEquals(new BigDecimal("77.906"), obs2.getValue());

        builder = new QueryBuilder(restTemplate, cacheProvider);

        observations = builder
            .series()
            .observations()
            .setApiKey(API_KEY)
            .setSeriesId("GNPA")
            .doGet(Observations.class);

        // assertEquals(1, updateCount.get());

        assertTrue(observations.getCount() >= 87);

        observationList = observations.getObservationList();

        obs2 = observationList.get(2);

        assertEquals(new BigDecimal("77.906"), obs2.getValue());

        // assertEquals(1, updateCount.get());
    }

    @Test
    public void getSeriesSearch() {

        QueryBuilder builder = new QueryBuilder(restTemplate);

        Seriess seriess = builder
            .series()
            .search()
            .setApiKey(API_KEY)
            // .setSearchText("money stock")
            // .setSearchType(SearchType.fullText)
            .setSearchText("GOLD")
            .setSearchType(SearchType.seriesId)
            // https://research.stlouisfed.org/docs/api/fred/realtime_period.html
            // .setRealtimeStart("1800-01-01")
            // .setRealtimeEnd("9999-12-31")
            .setLimit(1000)
            .setOffset(0)
            .setOrderBy(OrderBy.searchRank)
            .setSortOrder(SortOrder.desc)
            // .setFilterVariable(FilterVariable.frequency)
            // .setFilterValue(FilterValue.all)
            .doGet(Seriess.class);

        assertNotNull(seriess);
        // assertEquals(FilterValue.all, seriess.getFilterValue());
        assertEquals(OrderBy.searchRank, seriess.getOrderBy());
        assertEquals(SortOrder.desc, seriess.getSortOrder());
        assertTrue(seriess.getCount() > 20);
        // assertEquals(FilterValue.all, seriess.getFilterValue());
        assertEquals(0, seriess.getOffset());
        assertEquals(1000, seriess.getLimit());

        assertTrue(seriess.getSeriesList().size() > 2);
    }

    @Test
    public void plotRussell2000TotalMarketIndex() throws InvocationTargetException, InterruptedException, IOException {
        openWindow();

        final String seriesId = "DTWEXBGS";

        QueryBuilder builder = new QueryBuilder(restTemplate);

        Observations observations = builder
            .series()
            .observations()
            .setApiKey(API_KEY)
            .setSeriesId(seriesId)
            .setSortOrder(SortOrder.asc)
            .setOrderBy(OrderBy.observationDate)
            .doGet(Observations.class);

        assertTrue(observations.getCount() > 100);

        List<Observation> observationList = observations.getObservationList();

        Observation obs1 = observationList.get(0);
        Observation obs2 = observationList.get(1);

        assertEquals(new BigDecimal("101.4155"), obs1.getValue());
        assertEquals(new BigDecimal("100.7558"), obs2.getValue());

        final TimeSeries series = new TimeSeries(seriesId);
        RegularTimePeriod current = new Day();
        double startValue = observations.getObservationList().get(0).getValue().doubleValue();
        for (Observation obs : observations.getObservationList()) {
            if (obs.getValue() == null) {
                continue;
            }
            series.add(new Day(obs.getDate()), obs.getValue().doubleValue());
        }

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            seriesId,
            "Time",
            String.format("Value (Start=%.2f)", startValue),
            (XYDataset) new TimeSeriesCollection(series),
            false,
            false,
            false);
        
        File output = new File(TEMP_DIR, "plotRussell2000TotalMarketIndexDirect.png");
        ChartUtilities.saveChartAsPNG(output, chart, 640, 480);
        // @insert:image:plotRussell2000TotalMarketIndexDirect.png@

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        append(chartPanel);

        Screenshot.takeDesktopScreenshot("plotRussell2000TotalMarketIndex", true);
        waitForWindowClosingManual();
    }
}
