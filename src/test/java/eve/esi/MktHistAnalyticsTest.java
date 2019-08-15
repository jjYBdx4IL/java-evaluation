package eve.esi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.env.Surefire;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.model.MarketOrdersResponse;
import net.troja.eve.esi.model.TypeResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MktHistAnalyticsTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(MktHistAnalyticsTest.class);

    private static final String REGION = "Sinq Laison";
    private static final int MIN_AGE_DAYS = 10;
    private static final long MAX_MILLIS_SINCE_EPOCH = System.currentTimeMillis() - MIN_AGE_DAYS * 86400L * 1000L;
    private static final int MIN_TYPE_ORDER_COUNT = 20;

    private static final int W = 640;
    private static final int H = 480;

    ApiClient apiClient = null;
    ApiWrapper api = null;
    DataCache cache = null;

    @Before
    public void before() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());
        apiClient = ApiClientFactory.getClient();
        api = new ApiWrapper(apiClient);
        cache = new DataCache(apiClient);
        cache.loadRegions();
    }

    @Test
    public void test() throws Exception {

        List<MarketOrdersResponse> orders = api.dumpMarketOrders("all", cache.getRegion(REGION).getRegionId());
        assertFalse(orders.isEmpty());

        // filter #1
        List<MarketOrdersResponse> orders2 = new ArrayList<>();
        for (MarketOrdersResponse order : orders) {
            if (!order.getIsBuyOrder() && order.getIssued().toInstant().toEpochMilli() <= MAX_MILLIS_SINCE_EPOCH) {
                orders2.add(order);
            }
        }
        orders = orders2;

        // filter #2 - black list by market group (or parent market group)
        List<Integer> excludedMarketGroups = new ArrayList<>(Arrays.asList(2, 150)); // 2 - blueprints
        orders2 = new ArrayList<>();
        for (MarketOrdersResponse order : orders) {
            TypeResponse tr = cache.getType(order.getTypeId());
            if (!isInAnyMarketGroup(tr, excludedMarketGroups)) {
                orders2.add(order);
            }
        }
        orders = orders2;
        
        // filter #3 - remove orders created during downtime
        orders2 = new ArrayList<>();
        for (MarketOrdersResponse order : orders) {
            int hour = order.getIssued().getHour();
            int minute = order.getIssued().getMinute();
            if (hour == 11 && minute > 0 && minute < 30) {
//                continue;
            }
            orders2.add(order);
        }
        orders = orders2;

        // filter #4 - remove NPC orders based on duration
        orders2 = new ArrayList<>();
        for (MarketOrdersResponse order : orders) {
            if (ApiUtils.isNpcOrder(order)) {
                continue;
            }
            orders2.add(order);
        }
        orders = orders2;
        
        // compute the uneven-ness of the hour distribution per type
        Map<Integer, SummaryStatistics> sellOrdersIssuedByHourOfDayAndTypeSS = new HashMap<>();
        Map<Integer, int[]> sellOrdersIssuedByHourOfDayAndType = new HashMap<>();

        DefaultCategoryDataset sellOrdersIssuedByHourOfDayDS = new DefaultCategoryDataset();
        int[] sellOrdersIssuedByHourOfDay = new int[24];
        double[] sellOrdersIssuedByDay = new double[orders.size()];
        TObjectIntHashMap<Integer> typeCount = new TObjectIntHashMap<>();
        long dayMin = Long.MAX_VALUE;
        long dayMax = Long.MIN_VALUE;
        int i = 0;
        for (MarketOrdersResponse order : orders) {
            int hour = order.getIssued().getHour();
            int minute = order.getIssued().getMinute();

            sellOrdersIssuedByHourOfDay[hour]++;

            SummaryStatistics byType = sellOrdersIssuedByHourOfDayAndTypeSS.get(order.getTypeId());
            if (byType == null) {
                byType = new SummaryStatistics();
                sellOrdersIssuedByHourOfDayAndTypeSS.put(order.getTypeId(), byType);
            }
            byType.addValue(hour + minute / 60f);

            int[] byTypeArr = sellOrdersIssuedByHourOfDayAndType.get(order.getTypeId());
            if (byTypeArr == null) {
                byTypeArr = new int[24];
                sellOrdersIssuedByHourOfDayAndType.put(order.getTypeId(), byTypeArr);
            }
            byTypeArr[hour]++;

            long day = order.getIssued().toEpochSecond() / 86400;
            sellOrdersIssuedByDay[i] = day;
            if (day < dayMin) {
                dayMin = day;
            }
            if (day > dayMax) {
                dayMax = day;
            }

            typeCount.adjustOrPutValue(order.getTypeId(), 1, 1);

            i++;
        }
        final int len = i;
        for (i = 0; i < len; i++) {
            sellOrdersIssuedByDay[i] -= dayMin;
        }
        for (int hour = 0; hour < 24; hour++) {
            sellOrdersIssuedByHourOfDayDS.addValue(sellOrdersIssuedByHourOfDay[hour],
                "sell orders issued", "" + hour);
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "sell orders issued, by time of day",
            "hour of day",
            "frequency",
            sellOrdersIssuedByHourOfDayDS);
        ChartUtilities.saveChartAsPNG(new File(TEMP_DIR, "sellOrdersIssuedByHourOfDay.png"), chart, W, H);

        HistogramDataset dataset2 = new HistogramDataset();
        dataset2.addSeries("sell orders issued, by day", Arrays.copyOf(sellOrdersIssuedByDay, len),
            (int) (dayMax - dayMin + 1L), 0d, (double) (dayMax - dayMin));
        JFreeChart chart2 = ChartFactory.createHistogram(
            "sell orders issued, by day",
            "day",
            "frequency",
            dataset2,
            PlotOrientation.VERTICAL,
            false, false, false);
        ChartUtilities.saveChartAsPNG(new File(TEMP_DIR, "sellOrdersIssuedByDay.png"), chart2, W, H);

        // dump information about most frequent types
        List<Integer> types = new ArrayList<>(typeCount.keySet());
        Collections.sort(types, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(typeCount.get(o2), typeCount.get(o1));
            };
        });
        for (int typeId : types) {
            int count = typeCount.get(typeId);
            if (count < MIN_TYPE_ORDER_COUNT) {
                break;
            }
            TypeResponse tr = cache.getType(typeId);
            assertNotNull(tr);
            System.out.println(String.format("type %,d : %,d (%s, market group %,d %s, %s)",
                typeId, count, tr.getName(),
                tr.getMarketGroupId() != null ? tr.getMarketGroupId() : -1,
                tr.getMarketGroupId() != null ? cache.getMarketGroup(tr.getMarketGroupId()).getName() : "",
                StringUtils.join(getMarketGroupHierarchy(tr), " > ")
            ));
        }

        // dump a type-based ranking of how even sell orders are distributed
        // during the day
        List<Integer> ranking = new ArrayList<>(sellOrdersIssuedByHourOfDayAndTypeSS.keySet());
        Collections.sort(ranking, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                SummaryStatistics ss1 = sellOrdersIssuedByHourOfDayAndTypeSS.get(o1);
                SummaryStatistics ss2 = sellOrdersIssuedByHourOfDayAndTypeSS.get(o2);
                return -Double.compare(ss1.getStandardDeviation(), ss2.getStandardDeviation());
            }

        });
        for (int typeId : ranking) {
            SummaryStatistics ss1 = sellOrdersIssuedByHourOfDayAndTypeSS.get(typeId);
            if (ss1.getN() < MIN_TYPE_ORDER_COUNT) {
                continue;
            }
            System.out.println(
                String.format("%,d / sdev = %.2f / mean = %.2f / n = %,d", typeId, ss1.getStandardDeviation(),
                    ss1.getMean(), ss1.getN()));

            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            for (int hour = 0; hour < 24; hour++) {
                ds.addValue(sellOrdersIssuedByHourOfDayAndType.get(typeId)[hour], "sell orders issued",
                    "" + hour);
            }
            TypeResponse tr = cache.getType(typeId);
            chart = ChartFactory.createBarChart(
                String.format("sell orders issued, by time of day, type %,d (%s), market group %,d (%s, %s)",
                    typeId,
                    cache.getType(typeId).getName(),
                    tr.getMarketGroupId() != null ? tr.getMarketGroupId() : -1,
                    tr.getMarketGroupId() != null ? cache.getMarketGroup(tr.getMarketGroupId()).getName() : "",
                    StringUtils.join(getMarketGroupHierarchy(tr), " > ")
                ),
                "hour of day",
                "frequency",
                ds);
            ChartUtilities.saveChartAsPNG(
                new File(TEMP_DIR, String.format("sdev%.2f-type%d.png", ss1.getStandardDeviation(), typeId)),
                chart, W, H);
        }
    }

    private Map<Integer, List<Integer>> marketGroupHierarchyCache = new HashMap<>();

    private List<Integer> getMarketGroupHierarchy(TypeResponse tr)
        throws ClassNotFoundException, IOException, SQLException {

        if (tr.getMarketGroupId() == null) {
            return Collections.emptyList();
        }

        if (marketGroupHierarchyCache.containsKey(tr.getMarketGroupId())) {
            return marketGroupHierarchyCache.get(tr.getMarketGroupId());
        }

        List<Integer> result = new ArrayList<>();
        Integer currentGroupId = tr.getMarketGroupId();
        while (currentGroupId != null) {
            result.add(currentGroupId);
            currentGroupId = cache.getMarketGroup(currentGroupId).getParentGroupId();
        }

        marketGroupHierarchyCache.put(tr.getMarketGroupId(), result);

        return result;
    }

    private boolean isInAnyMarketGroup(TypeResponse tr, List<Integer> marketGroups)
        throws ClassNotFoundException, IOException, SQLException {
        List<Integer> hier = getMarketGroupHierarchy(tr);
        return !Collections.disjoint(hier, marketGroups);
    }
}
