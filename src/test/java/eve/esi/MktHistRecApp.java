package eve.esi;

import static org.junit.Assert.assertNotEquals;

import com.github.jjYBdx4IL.utils.env.Maven;
import eve.esi.DataCache.MktHistEntry;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.model.MarketOrdersResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class MktHistRecApp {

    private static final File TEMP_DIR = Maven.getTempTestDir(MktHistRecApp.class);
    private static final Logger LOG = LoggerFactory.getLogger(MktHistRecApp.class);
    private static final String REGION = "The Forge";

    private static final int W = 640;
    private static final int H = 480;

    ApiClient apiClient = null;
    ApiWrapper api = null;
    DataCache cache = null;
    
    private void init() throws Exception {
        // sql timestamps are time zone sensitive (argh)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        apiClient = ApiClientFactory.getClient();
        api = new ApiWrapper(apiClient);
        cache = new DataCache(apiClient);
    }
    
    public void run() throws Exception {
        LOG.info("starting");
        init();
        cache.loadRegions();

        Date lmod = new Date(0);
        LOG.info("started");
        while (true) {
            List<MarketOrdersResponse> orders = api.dumpMarketOrders("all", cache.getRegion(REGION).getRegionId(),
                lmod);
            if (orders == null) {
                ApiUtils.retryDelay(30);
                continue;
            }
            assertNotEquals(0, lmod.getTime());

            LOG.info(String.format("merging %,d market orders", orders.size()));
            cache.merge(orders, lmod);
            LOG.info("Done.");
        }
    }

    public static void main(String[] args) throws Exception {
        new MktHistRecApp().run();
    }

    @Test
    public void doAnalytics() throws Exception {
        init();

        int count = 0;
        OffsetDateTime firstClosed = null;
        OffsetDateTime lastClosed = null;
        Iterator<MktHistEntry> it = cache.mktHistIt(false);
        while (it.hasNext()) {
            MktHistEntry mhe = it.next();
            if (mhe.closed != null && (firstClosed == null || mhe.closed.isBefore(firstClosed))) {
                firstClosed = mhe.closed;
            }
            if (mhe.closed != null && (lastClosed == null || mhe.closed.isAfter(lastClosed))) {
                lastClosed = mhe.closed;
            }
            count++;
        }
        System.out.println("number of market orders: " + count);
        System.out.println("first order closed: " + firstClosed);
        System.out.println("last order closed: " + lastClosed);
        OffsetDateTime rangeStart = firstClosed.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);
        OffsetDateTime rangeEnd = lastClosed.truncatedTo(ChronoUnit.DAYS);
        if (!rangeStart.isBefore(rangeEnd)) {
            throw new IllegalStateException();
        }
        System.out.println("range: " + rangeStart + " ... " + rangeEnd);
        
        
        int[] buyOrdersIssuedByHourOfDay = new int[24];
        int[] buyOrdersClosedByHourOfDay = new int[24];
        int[] sellOrdersIssuedByHourOfDay = new int[24];
        int[] sellOrdersClosedByHourOfDay = new int[24];
        
        it = cache.mktHistIt(true);
        while (it.hasNext()) {
            MktHistEntry mhe = it.next();
            
            // no npc orders
            if (ApiUtils.isNpcOrder(mhe.mor)) {
                continue;
            }
            if (mhe.mor.getIsBuyOrder()) {
                // count new order creations
                if (!mhe.issued.isBefore(rangeStart) && mhe.issued.isBefore(rangeEnd)) {
                    buyOrdersIssuedByHourOfDay[mhe.issued.get(ChronoField.HOUR_OF_DAY)]++;
                }
                
                // count order completions/deletions
                if (mhe.closed != null && !mhe.closed.isBefore(rangeStart) && mhe.closed.isBefore(rangeEnd)) {
                    buyOrdersClosedByHourOfDay[mhe.closed.get(ChronoField.HOUR_OF_DAY)]++;
                }
            } else {
                // count new order creations
                if (!mhe.issued.isBefore(rangeStart) && mhe.issued.isBefore(rangeEnd)) {
                    sellOrdersIssuedByHourOfDay[mhe.issued.get(ChronoField.HOUR_OF_DAY)]++;
                }
                
                // count order completions/deletions
                if (mhe.closed != null && !mhe.closed.isBefore(rangeStart) && mhe.closed.isBefore(rangeEnd)) {
                    sellOrdersClosedByHourOfDay[mhe.closed.get(ChronoField.HOUR_OF_DAY)]++;
                }
            }
        }
        
        DefaultCategoryDataset sellOrdersByHourOfDayDS = new DefaultCategoryDataset();
        for (int hour = 0; hour < 24; hour++) {
            sellOrdersByHourOfDayDS.addValue(sellOrdersIssuedByHourOfDay[hour],
                "sell orders issued", "" + hour);
            sellOrdersByHourOfDayDS.addValue(sellOrdersClosedByHourOfDay[hour],
                "sell orders closed", "" + hour);
        }
        JFreeChart chart = ChartFactory.createBarChart(
            "sell orders, by time of day",
            "hour of day",
            "frequency",
            sellOrdersByHourOfDayDS);
        ChartUtilities.saveChartAsPNG(new File(TEMP_DIR, "sellOrdersByHourOfDay.png"), chart, W, H);
        
        DefaultCategoryDataset netSellOrdersByHourOfDayDS = new DefaultCategoryDataset();
        for (int hour = 0; hour < 24; hour++) {
            netSellOrdersByHourOfDayDS.addValue(sellOrdersIssuedByHourOfDay[hour] - sellOrdersClosedByHourOfDay[hour],
                "net sell orders issued", "" + hour);
        }
        chart = ChartFactory.createBarChart(
            "net sell orders issued, by time of day",
            "hour of day",
            "frequency",
            netSellOrdersByHourOfDayDS);
        ChartUtilities.saveChartAsPNG(new File(TEMP_DIR, "netSellOrdersByHourOfDay.png"), chart, W, H);
        
        DefaultCategoryDataset buyOrdersByHourOfDayDS = new DefaultCategoryDataset();
        for (int hour = 0; hour < 24; hour++) {
            buyOrdersByHourOfDayDS.addValue(buyOrdersIssuedByHourOfDay[hour],
                "buy orders issued", "" + hour);
            buyOrdersByHourOfDayDS.addValue(buyOrdersClosedByHourOfDay[hour],
                "buy orders closed", "" + hour);
        }
        chart = ChartFactory.createBarChart(
            "buy orders, by time of day",
            "hour of day",
            "frequency",
            buyOrdersByHourOfDayDS);
        ChartUtilities.saveChartAsPNG(new File(TEMP_DIR, "buyOrdersByHourOfDay.png"), chart, W, H);
        
        DefaultCategoryDataset netBuyOrdersByHourOfDayDS = new DefaultCategoryDataset();
        for (int hour = 0; hour < 24; hour++) {
            netBuyOrdersByHourOfDayDS.addValue(buyOrdersIssuedByHourOfDay[hour] - buyOrdersClosedByHourOfDay[hour],
                "net buy orders issued", "" + hour);
        }
        chart = ChartFactory.createBarChart(
            "net buy orders issued, by time of day",
            "hour of day",
            "frequency",
            netBuyOrdersByHourOfDayDS);
        ChartUtilities.saveChartAsPNG(new File(TEMP_DIR, "netBuyOrdersByHourOfDay.png"), chart, W, H);
    }
}
