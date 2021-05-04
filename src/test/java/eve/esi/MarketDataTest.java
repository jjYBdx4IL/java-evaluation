package eve.esi;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterInfo;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterShipResponse;
import net.troja.eve.esi.model.ConstellationResponse;
import net.troja.eve.esi.model.MarketOrdersResponse;
import net.troja.eve.esi.model.StationResponse;
import net.troja.eve.esi.model.StructureResponse;
import net.troja.eve.esi.model.SystemResponse;
import net.troja.eve.esi.model.TypeDogmaAttribute;
import net.troja.eve.esi.model.TypeResponse;
import pushbullet.PushbulletUtils;

/**
 * https://github.com/burberius/eve-esi https://esi.evetech.net/ui/
 *
 */
public class MarketDataTest {

    private static final Logger LOG = LoggerFactory.getLogger(MarketDataTest.class);

    private ApiClient apiClient = null;
    private ApiWrapper api = null;
    private DataCache cache = null;
    private double salesTax = .02d;
    private double maxVolume = 30800;
    private double minProfit = 2e6;
    private double minProfitNotify = 20e6;
    private double minBuyersAtProfitVolFactor = 5d;
    // "The Forge", "Sinq Laison", "Metropolis", "Everyshore", "The Citadel",
    // "Domain"
  private final String[] regions = new String[] { "The Forge", "Sinq Laison" };
//    private final String[] regions = new String[] { "The Forge", "Sinq Laison", "Everyshore" };
//    private final String[] regions = new String[] { "The Forge", "Everyshore" };
    // private final String[] regions = new String[] { "Derelik", "Metropolis"
    // };
//    private final String[] regions = new String[] { "Domain", "The Forge" };
    private boolean filterSalesByCurrentLocation = false;
    private boolean filterBuysByCurrentLocation = false;
    // match only sell and buy orders originating from the same location:
    private boolean sameStationOnly = false;
    private boolean filterSalesByCurrrentStation = false;
    private boolean filterBuysByCurrrentStation = false;
    private CharacterLocationResponse myCurrentLocation = null;
    private long currentLocationId = -1;
    private SystemResponse myCurrentSystem = null;
    private ConstellationResponse myCurrentConstellation = null;
    private double cash;

    @Test
    public void test() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());

        apiClient = ApiClientFactory.getClient();
        cache = new DataCache(apiClient);
        api = new ApiWrapper(apiClient);

        cache.loadRegions();
//        cache.loadTypeData();
        cache.loadSystems();
        cache.loadStructures();
        cache.loadStations();
        cache.loadConstellations();

        for (;;) {
            updateMyStatus();

            List<MarketOrdersResponse> result = new ArrayList<MarketOrdersResponse>();
            for (String region : regions) {
                addMarketOrdersForRegion(region, result);
            }

            addVirtualCompressedOreSales(result);

            List<Trade> trades = findDeals(result, cash);

            // show top ten trades
            Collections.sort(trades);
            int count = 10;
            for (Trade trade : trades) {
                LOG.info(trade.toString());

                if (--count == 0) {
                    break;
                }
            }

            if (!trades.isEmpty() && trades.get(0).profit >= minProfitNotify) {
                PushbulletUtils.sendMessage(String.format("Trade found: %,.0f", trades.get(0).profit));
            } else {
                LOG.info("No acceptable trade found.");
            }

            Thread.sleep(120000L);
        }
    }

    private void updateMyStatus() throws ApiException, ClassNotFoundException, SQLException, IOException {
        CharacterInfo info = api.getCharInfo();
        LOG.info(String.format("%s (id %s)", info.getCharacterName(), info.getCharacterID()));
        LOG.info(String.format("max cargo volume: %,d m3", (long) maxVolume));
        LOG.info(String.format("sales tax: %.1f%%", salesTax * 100));
        LOG.info(String.format("min profit: %,.0f isk", minProfit));
        LOG.info(String.format("minBuyersAtProfitVolFactor: %.1f", minBuyersAtProfitVolFactor));

        CharacterShipResponse shipRes = api.getActiveShip();
        long shipId = shipRes.getShipItemId();
        LOG.info("current ship: " + shipId);

        cash = api.getCashBalance();
        LOG.info(String.format("cash: %,.2f isk", cash));

        myCurrentLocation = api.getCurrentLocation();
        currentLocationId = ApiUtils.toGenericLocationId(myCurrentLocation);
        myCurrentSystem = cache.getSystem(myCurrentLocation.getSolarSystemId());
        myCurrentConstellation = cache.getConstellation(myCurrentSystem.getConstellationId());
        LOG.info(String.format("location: %s/%s", cache.getRegion(myCurrentConstellation.getRegionId()).getName(),
            myCurrentSystem.getName()));
    }

    private void addVirtualCompressedOreSales(List<MarketOrdersResponse> result) throws ClassNotFoundException, SQLException, IOException {
        List<MarketOrdersResponse> newOrders = new ArrayList<>();
        for (MarketOrdersResponse order : result) {
            if (order.getIsBuyOrder()) {
                continue;
            }

            CompressionData cd = getCompressionRatio(order.getTypeId());
            if (cd == null) {
                continue;
            }

            MarketOrdersResponse copyRes = clone(order);
            copyRes.typeId(cd.compressedTypeId);
            copyRes.volumeRemain(order.getVolumeRemain() / (int) cd.amountRequired);
            copyRes.volumeTotal(order.getVolumeTotal() / (int) cd.amountRequired);
            copyRes.price(order.getPrice() * cd.amountRequired);
            newOrders.add(copyRes);
        }
        result.addAll(newOrders);
    }

    static class VirtualOrder extends MarketOrdersResponse {

        private static final long serialVersionUID = 1L;

        VirtualOrder() {
            super();
        }
    }

    private MarketOrdersResponse clone(MarketOrdersResponse o) {
        return new VirtualOrder().duration(o.getDuration()).isBuyOrder(o.getIsBuyOrder()).issued(o.getIssued())
            .locationId(o.getLocationId()).minVolume(o.getMinVolume()).orderId(o.getOrderId()).price(o.getPrice())
            .range(o.getRange()).systemId(o.getSystemId()).typeId(o.getTypeId()).volumeRemain(o.getVolumeRemain())
            .volumeTotal(o.getVolumeTotal());
    }

    // dogma attribute id 1941 is amount required for compression
    private CompressionData getCompressionRatio(int typeId) throws ClassNotFoundException, SQLException, IOException {
        TypeResponse typeRes = cache.getType(typeId);
        int compressedTypeId = -1;
        double amountRequired = -1;
        for (TypeDogmaAttribute attr : typeRes.getDogmaAttributes()) {
            if (attr.getAttributeId() == 1940) {
                compressedTypeId = attr.getValue().intValue();
            }
            if (attr.getAttributeId() == 1941) {
                amountRequired = attr.getValue();
            }
        }
        if (compressedTypeId >= 0 && amountRequired >= 0) {
            return new CompressionData(typeId, compressedTypeId, amountRequired);
        }
        return null;
    }

    static class CompressionData {
        final int compressedTypeId;
        final int uncompressedTypeId;
        final double amountRequired;

        CompressionData(int uncompressedTypeId, int compressedTypeId, double amountRequired) {
            this.uncompressedTypeId = uncompressedTypeId;
            this.compressedTypeId = compressedTypeId;
            this.amountRequired = amountRequired;
        }
    }

    private List<Trade> findDeals(List<MarketOrdersResponse> orders, double cash) throws ClassNotFoundException, SQLException, IOException {
        // group by typeId
        Map<Integer, List<MarketOrdersResponse>> salesByType = new HashMap<>();
        Map<Integer, List<MarketOrdersResponse>> buysByType = new HashMap<>();
        for (MarketOrdersResponse order : orders) {
            if (order.getIsBuyOrder()) {
                List<MarketOrdersResponse> ordersForType = buysByType.get(order.getTypeId());
                if (ordersForType == null) {
                    ordersForType = new ArrayList<>();
                    buysByType.put(order.getTypeId(), ordersForType);
                }
                // ignore orders with minimum volumes for now
                if (order.getMinVolume() <= 1 && cache.getSystem(order.getSystemId()).getSecurityStatus() >= 0.5) {
                    ordersForType.add(order);
                }
            } else {
                List<MarketOrdersResponse> ordersForType = salesByType.get(order.getTypeId());
                if (ordersForType == null) {
                    ordersForType = new ArrayList<>();
                    salesByType.put(order.getTypeId(), ordersForType);
                }
                // ignore orders with minimum volumes for now
                if (order.getMinVolume() <= 1 && cache.getSystem(order.getSystemId()).getSecurityStatus() >= 0.5) {
                    ordersForType.add(order);
                }
            }
        }

        List<Trade> trades = new ArrayList<>();
        for (Integer typeId : buysByType.keySet()) {
            if (!salesByType.containsKey(typeId)) {
                continue;
            }
            double volumePerItem = cache.getType(typeId).getPackagedVolume();
            Trade best = null;
            for (MarketOrdersResponse sale : salesByType.get(typeId)) {
                for (MarketOrdersResponse buy : buysByType.get(typeId)) {
                    if (sameStationOnly && sale.getLocationId() != buy.getLocationId()) {
                        continue;
                    }

                    long count = Math.min(sale.getVolumeRemain(), buy.getVolumeRemain());
                    if (count * volumePerItem > maxVolume) {
                        count = (long) (maxVolume / volumePerItem);
                    }
                    if (count * sale.getPrice() > cash) {
                        count = (long) (cash / sale.getPrice());
                    }
                    double profit = count * getProfitPerItem(sale, buy);
                    if (profit >= minProfit && (best == null || profit > best.profit)) {
                        if (minBuyersAtProfitVolFactor <= 0d
                            || getBuyVolumeAtProfit(sale, buysByType.get(typeId)) >= count * minBuyersAtProfitVolFactor)
                            best = new Trade(sale, buy, count, profit, buysByType.get(typeId));
                    }
                }
            }
            if (best != null) {
                trades.add(best);
            }
        }

        return trades;
    }

    private double getBuyVolumeAtProfit(MarketOrdersResponse sale, List<MarketOrdersResponse> buys) {
        double totalVolume = 0;
        for (MarketOrdersResponse buy : buys) {
            if (getProfitPerItem(sale, buy) <= 0d) {
                continue;
            }
            totalVolume += buy.getVolumeRemain();
        }
        return totalVolume;
    }

    private double getProfitPerItem(MarketOrdersResponse sale, MarketOrdersResponse buy) {
        return buy.getPrice() * (1 - salesTax) - sale.getPrice();
    }

    public String getLocationDesc(long stationId) throws ClassNotFoundException, SQLException, IOException {
        for (;;) {
            try {
                return _getLocationDesc(stationId);
            } catch (ApiException e) {
                LOG.error("" + stationId, e);
                if ("Not Found".equals(e.getMessage())) {
                    return Long.toString(stationId);
                }
                if (!"Bad Gateway".equals(e.getMessage())) {
                    throw new RuntimeException(e);
                }
                ApiUtils.retryDelay();
            }
        }
    }

    private Map<Long, String> locationDescCache = new HashMap<>();

    /**
     * 
     * @param locationId
     *            either station or structure id
     * @return
     * @throws ApiException
     * @throws IOException 
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    public String _getLocationDesc(long locationId) throws ApiException, ClassNotFoundException, SQLException, IOException {
        if (locationDescCache.containsKey(locationId)) {
            return locationDescCache.get(locationId);
        }

        int systemId = -1;
        String stationName = null;

        if (ApiUtils.isStation(locationId)) {
            StationResponse stationRes = cache.getStation((int) locationId);
            systemId = stationRes.getSystemId();
            stationName = stationRes.getName();
        } else if (ApiUtils.isSystem(locationId)) {
            systemId = (int) locationId;
            stationName = cache.getSystem(systemId).getName();
        } else {
            StructureResponse structureRes = cache.getStructure(locationId);
            systemId = structureRes.getSolarSystemId();
            stationName = "*" + structureRes.getName();
        }

        SystemResponse sysRes = cache.getSystem(systemId);
        ConstellationResponse constellationRes = cache.getConstellation(sysRes.getConstellationId());
        String region = cache.getRegion(constellationRes.getRegionId()).getName();

        String result = String.format(Locale.ROOT, "%s/%s", region, stationName);
        locationDescCache.put(locationId, result);
        return result;
    }

    private class Trade implements Comparable<Trade> {
        final MarketOrdersResponse sale, buy;
        final long count;
        final double profit;
        final List<MarketOrdersResponse> buyList;

        Trade(MarketOrdersResponse sale, MarketOrdersResponse buy, long count, double profit,
            List<MarketOrdersResponse> buyersList) {
            this.sale = sale;
            this.buy = buy;
            this.count = count;
            this.profit = profit;
            this.buyList = buyersList;
        }

        double getItemsVolume() throws ClassNotFoundException, SQLException, IOException {
            return count * MarketDataTest.this.cache.getType(sale.getTypeId()).getPackagedVolume();
        }

        double getCapital() {
            return count * sale.getPrice();
        }

        @Override
        public int compareTo(Trade o) {
            return Double.compare(o.profit, profit);
        }

        @Override
        public String toString() {
            try {
                return String.format(Locale.ROOT,
                    "%s%s ==> %s%n  profit=%,d isk, %s, %,d m3, count=%,d, car=%,d isk, %,d/%,d @ %,.2f isk%n%s",
                    sale instanceof VirtualOrder ? "**VirtualOrder**   " : "",
                    MarketDataTest.this.getLocationDesc(sale.getLocationId()),
                    MarketDataTest.this.getLocationDesc(buy.getLocationId()),
                    (long) profit,
                    MarketDataTest.this.cache.getType(sale.getTypeId()).getName(),
                    (long) getItemsVolume(),
                    count,
                    (long) getCapital(),
                    sale.getVolumeRemain(), sale.getVolumeTotal(), sale.getPrice(),
                    getBuyersList(-1, "    "));
            } catch (ClassNotFoundException | SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        private String getBuyersList(int topN, String indent) throws ClassNotFoundException, SQLException, IOException {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            Collections.sort(buyList, new Comparator<MarketOrdersResponse>() {
                public int compare(MarketOrdersResponse o1, MarketOrdersResponse o2) {
                    return Double.compare(o2.getPrice(), o1.getPrice());
                };
            });
            for (MarketOrdersResponse buy : buyList) {
                double relProfit = ((1 - MarketDataTest.this.salesTax) * buy.getPrice() - sale.getPrice())
                    / sale.getPrice();
                if (buy.getVolumeRemain() < this.count / 10d) {
                    continue;
                }
                if (relProfit < -.5d) {
                    continue;
                }
                sb.append(indent);
                sb.append(String.format("%+6.1f%%\t%,d/%,d @ %,.2f isk\t%s%n",
                    relProfit * 100,
                    buy.getVolumeRemain(), buy.getVolumeTotal(),
                    buy.getPrice(), getLocationDesc(buy.getLocationId())));
                if (++i >= topN && topN != -1) {
                    break;
                }
            }
            return sb.toString();
        }
    }

    private void addMarketOrdersForRegion(String regionName, List<MarketOrdersResponse> result) throws ApiException, ClassNotFoundException, SQLException, IOException {
        int region = cache.getRegion(regionName).getRegionId();

        boolean includeBuys = !filterBuysByCurrentLocation || myCurrentConstellation.getRegionId() == region;
        boolean includeSales = !filterSalesByCurrentLocation || myCurrentConstellation.getRegionId() == region;

        List<MarketOrdersResponse> orders = api.dumpMarketOrders("all", region);

        for (MarketOrdersResponse order : orders) {
            if (includeBuys && order.getIsBuyOrder() || includeSales && !order.getIsBuyOrder()) {
                if (filterByCurrentLocation(order)) {
                    result.add(order);
                }
            }
        }

        LOG.info(String.format(Locale.ROOT, "%s: %,d orders", regionName, orders.size()));
    }

    private boolean filterByCurrentLocation(MarketOrdersResponse order) {
        if (filterBuysByCurrrentStation && order.getIsBuyOrder()) {
            if (order.getLocationId() != currentLocationId) {
                return false;
            }
        }
        if (filterSalesByCurrrentStation && !order.getIsBuyOrder()) {
            if (order.getLocationId() != currentLocationId) {
                return false;
            }
        }
        return true;
    }

}
