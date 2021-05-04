package eve.esi;

import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.jdbc.ResultSetUtils;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CategoryResponse;
import net.troja.eve.esi.model.ConstellationResponse;
import net.troja.eve.esi.model.MarketGroupResponse;
import net.troja.eve.esi.model.MarketOrdersResponse;
import net.troja.eve.esi.model.RegionResponse;
import net.troja.eve.esi.model.StationResponse;
import net.troja.eve.esi.model.StructureResponse;
import net.troja.eve.esi.model.SystemResponse;
import net.troja.eve.esi.model.TypeResponse;
import org.apache.sis.internal.jdk7.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.java.sql.DbUtils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DataCache {

    private static final Logger LOG = LoggerFactory.getLogger(DataCache.class);
    private static final File CACHE_DIR = new File(Maven.getMavenBuildDir(DataCache.class), DataCache.class.getName());
    private static final File DB_FILE = new File(CACHE_DIR, "h2db");

    private final ApiWrapper api;
    private final DbUtils dbUtils;
    private final Connection conn;
    private PreparedStatement psLastSync;
    private PreparedStatement psSetLastSync;
    private PreparedStatement psAdd;
    private PreparedStatement psGet;
    private PreparedStatement psGetByTypeAndName;
    private PreparedStatement psMktHistGetOrder;
    private PreparedStatement psMktHistGetAll;
    private PreparedStatement psMktHistGetLatestPayload;
    private PreparedStatement psMktHistAddPayload;
    private PreparedStatement psMktHistAdd;
    private PreparedStatement psMktHistGetOpenOrderIds;
    private PreparedStatement psMktHistCloseOrder;
    private final MessageDigest messageDigest;

    public DataCache(ApiClient apiClient) {
        this.api = new ApiWrapper(apiClient);
        if (!CACHE_DIR.exists()) {
            CACHE_DIR.mkdir();
        }
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            dbUtils = DbUtils.getInstance(DB_FILE);
            conn = dbUtils.getConnection();
            conn.setAutoCommit(false);
            initDb();
        } catch (ClassNotFoundException | SQLException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void initDb() throws SQLException {
        if (!dbUtils.existsTable("DATACACHE")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE DATACACHE("
                    + "ID BIGINT, TYPE INT, NAME VARCHAR(255), DATA VARCHAR(1000000), PRIMARY KEY (TYPE,ID))");
                stmt.execute("CREATE INDEX DATACACHE_IDX_1 ON DATACACHE(TYPE,NAME)");
            }
        }
        if (!dbUtils.existsTable("LASTSYNC")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE LASTSYNC("
                    + "TYPE VARCHAR(255) PRIMARY KEY, LASTSYNC BIGINT)");
            }
        }
        psLastSync = conn.prepareStatement("SELECT LASTSYNC FROM LASTSYNC WHERE TYPE = ?");
        psSetLastSync = conn.prepareStatement("MERGE INTO LASTSYNC (TYPE, LASTSYNC) VALUES (?, ?)");
        psAdd = conn.prepareStatement("MERGE INTO DATACACHE (ID, TYPE, NAME, DATA) VALUES (?, ?, ?, ?)");
        psGet = conn.prepareStatement("SELECT DATA FROM DATACACHE WHERE TYPE = ? AND ID = ?");
        psGetByTypeAndName = conn.prepareStatement("SELECT ID, DATA FROM DATACACHE WHERE TYPE = ? AND NAME LIKE ?");

//         try (Statement stmt = conn.createStatement()) {
//         stmt.execute("DROP TABLE MKTHIST");
//         stmt.execute("DROP TABLE MKTHISTPAYLOAD");
//         }

        // market orders time line cache
        if (!dbUtils.existsTable("MKTHIST")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE MKTHIST("
                    + "ORDERID BIGINT PRIMARY KEY NOT NULL, ISSUED SMALLDATETIME NOT NULL, CLOSED SMALLDATETIME, DIGEST BINARY(16))");
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE MKTHISTPAYLOAD("
                    + "ORDERID BIGINT NOT NULL, CREATED SMALLDATETIME NOT NULL, JSON VARCHAR(1000000), PRIMARY KEY(ORDERID,CREATED))");
            }
        }

        psMktHistGetOrder = conn.prepareStatement(
            "SELECT ORDERID, ISSUED, CLOSED, DIGEST FROM MKTHIST WHERE ORDERID = ?");
        psMktHistGetAll = conn.prepareStatement(
            "SELECT ORDERID, ISSUED, CLOSED, DIGEST FROM MKTHIST");
        psMktHistGetLatestPayload = conn.prepareStatement(
            "SELECT TOP 1 ORDERID, CREATED, JSON FROM MKTHISTPAYLOAD WHERE ORDERID = ? ORDER BY CREATED DESC");
        psMktHistAdd = conn.prepareStatement(
            "MERGE INTO MKTHIST (ORDERID, ISSUED, CLOSED, DIGEST) VALUES (?, ?, ?, ?)");
        psMktHistAddPayload = conn.prepareStatement(
            "MERGE INTO MKTHISTPAYLOAD ( ORDERID, CREATED, JSON ) VALUES (?, ?, ?)");
        psMktHistGetOpenOrderIds = conn.prepareStatement(
            "SELECT ORDERID FROM MKTHIST WHERE CLOSED IS NULL ORDER BY ORDERID ASC");
        psMktHistCloseOrder = conn.prepareStatement(
            "UPDATE MKTHIST SET CLOSED = ? WHERE ORDERID = ? AND CLOSED IS NULL");
    }

    public static class MktHistEntry {
        public long orderId;
        public OffsetDateTime issued;
        public OffsetDateTime closed;
        public byte[] jsonDigest;
        public OffsetDateTime jsonCreated;
        public MarketOrdersResponse mor;
    }
    
    public Iterator<MktHistEntry> mktHistIt(boolean fetchMarketOrdersResponse) throws SQLException {
        final MktHistEntry mhe = new MktHistEntry();
        final ResultSet rs = psMktHistGetAll.executeQuery();
        rs.setFetchSize(1000);
        return new Iterator<MktHistEntry>() {
            @Override
            public boolean hasNext() {
                try {
                    if (rs.isClosed()) {
                        return false;
                    }
                    if (rs.isAfterLast()) {
                        rs.close();
                        return false;
                    }
                    return true;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public MktHistEntry next() {
                try {
                    if (rs.next()) {
                        mhe.orderId = rs.getLong("ORDERID");
                        mhe.issued = rs.getTimestamp("ISSUED").toInstant().atOffset(ZoneOffset.UTC);
                        Date closed = rs.getTimestamp("CLOSED");
                        mhe.closed = closed == null ? null : closed.toInstant().atOffset(ZoneOffset.UTC);
                        mhe.jsonDigest = rs.getBytes("DIGEST");
                        if (!fetchMarketOrdersResponse) {
                            mhe.jsonCreated = null;
                            mhe.mor = null;
                        } else {
                            psMktHistGetLatestPayload.setLong(1, mhe.orderId);
                            try (ResultSet rs2 = psMktHistGetLatestPayload.executeQuery()) {
                                rs2.next();
                                mhe.jsonCreated = rs2.getTimestamp("CREATED").toInstant().atOffset(ZoneOffset.UTC);
                                mhe.mor = decode(rs2.getString("JSON"), MarketOrdersResponse.class);
                            }
                        }
                        if (rs.isLast()) {
                            rs.close();
                        }
                        return mhe;
                    } else {
                        throw new RuntimeException("access past end of iterator");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private long getLastSync(Class<?> type) throws SQLException {
        psLastSync.setString(1, type.getName());
        try (ResultSet rs = psLastSync.executeQuery()) {
            return rs.next() ? rs.getLong(1) : -1;
        }
    }

    private void setLastSync(Class<?> type, long timestamp) throws SQLException {
        psSetLastSync.setString(1, type.getName());
        psSetLastSync.setLong(2, timestamp);
        psSetLastSync.execute();
    }

    private String encode(Object obj) {
        return api.serialize(obj);
    }

    private <T> T decode(String json, Class<T> type) {
        return api.deserialize(json, type);
    }

    private <T> T decodeFirst(Class<T> type, long id) throws SQLException, ClassNotFoundException, IOException {
        psGet.setInt(1, dbType(type));
        psGet.setLong(2, id);
        try (ResultSet rs = psGet.executeQuery()) {
            if (!rs.next()) {
                // throw new RuntimeException("not found: type=" +
                // type.getSimpleName() + ", id=" + id);
                return null;
            }
            return decode(rs.getString(1), type);
        }
    }

    private <T> T decodeFirst(Class<T> type, String name) throws SQLException, ClassNotFoundException, IOException {
        psGetByTypeAndName.setInt(1, dbType(type));
        psGetByTypeAndName.setString(2, name);
        try (ResultSet rs = psGetByTypeAndName.executeQuery()) {
            if (!rs.next()) {
                return null;
            }
            return decode(rs.getString(2), type);
        }
    }

    private <T> Map<Long, T> decodeAll(Class<T> type, String name)
        throws SQLException, ClassNotFoundException, IOException {
        psGetByTypeAndName.setInt(1, dbType(type));
        psGetByTypeAndName.setString(2, name);
        Map<Long, T> result = new HashMap<>();
        try (ResultSet rs = psGetByTypeAndName.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getLong(1), decode(rs.getString(2), type));
            }
        }
        return result;
    }

    public void loadTypeData() throws ApiException, IOException, SQLException {
        if (getLastSync(TypeResponse.class) > 0) {
            return;
        }

        LOG.info("fetching types data");
        long now = System.currentTimeMillis();
        Map<Integer, TypeResponse> typeData = api.dumpTypes();
        for (Entry<Integer, TypeResponse> e : typeData.entrySet()) {
            dbAdd(e.getKey(), e.getValue());
        }
        setLastSync(TypeResponse.class, now);
        conn.commit();
        LOG.info(String.format(Locale.ROOT, "imported %d types", typeData.size()));
    }

    private void dbAdd(long id, TypeResponse sr) throws IOException, SQLException {
        psAdd.setLong(1, id);
        psAdd.setInt(2, dbType(TypeResponse.class));
        psAdd.setString(3, sr.getName());
        psAdd.setString(4, encode(sr));
        psAdd.execute();
    }

    public TypeResponse getType(int id) throws SQLException, ClassNotFoundException, IOException {
        TypeResponse sr = decodeFirst(TypeResponse.class, id);
        if (sr == null) {
            sr = api.getType(id);
            dbAdd(id, sr);
            conn.commit();
        }
        return sr;
    }

    public void loadRegions() throws IOException, ApiException, SQLException {
        if (getLastSync(RegionResponse.class) > 0) {
            return;
        }

        LOG.info("fetching regions data");
        long now = System.currentTimeMillis();
        Map<Integer, RegionResponse> regions = api.dumpRegions();
        for (Entry<Integer, RegionResponse> e : regions.entrySet()) {
            psAdd.setLong(1, (long) e.getValue().getRegionId());
            psAdd.setInt(2, dbType(RegionResponse.class));
            psAdd.setString(3, e.getValue().getName());
            psAdd.setString(4, encode(e.getValue()));
            psAdd.execute();
        }
        setLastSync(RegionResponse.class, now);
        conn.commit();
        LOG.info(String.format(Locale.ROOT, "imported %d regions", regions.size()));
    }

    public RegionResponse getRegion(int regionId) throws ClassNotFoundException, SQLException, IOException {
        return decodeFirst(RegionResponse.class, regionId);
    }

    public RegionResponse getRegion(String regionName) throws ClassNotFoundException, SQLException, IOException {
        return decodeFirst(RegionResponse.class, regionName);
    }

    public void loadConstellations() throws IOException, ApiException, SQLException {
        if (getLastSync(ConstellationResponse.class) > 0) {
            return;
        }

        LOG.info("fetching constellations data");
        long now = System.currentTimeMillis();
        Map<Integer, ConstellationResponse> constellations = api.dumpConstellations();
        for (Entry<Integer, ConstellationResponse> e : constellations.entrySet()) {
            psAdd.setLong(1, (long) e.getValue().getConstellationId());
            psAdd.setInt(2, dbType(ConstellationResponse.class));
            psAdd.setString(3, e.getValue().getName());
            psAdd.setString(4, encode(e.getValue()));
            psAdd.execute();
        }
        setLastSync(ConstellationResponse.class, now);
        conn.commit();
        LOG.info(String.format(Locale.ROOT, "imported %d constellations", constellations.size()));
    }

    public ConstellationResponse getConstellation(int id) throws ClassNotFoundException, SQLException, IOException {
        return decodeFirst(ConstellationResponse.class, id);
    }

    public void loadStructures() throws IOException, ApiException, SQLException {
        if (getLastSync(StructureResponse.class) > 0) {
            return;
        }

        LOG.info("fetching structures data");
        long now = System.currentTimeMillis();
        Map<Long, StructureResponse> structures = api.dumpStructures();
        for (Entry<Long, StructureResponse> e : structures.entrySet()) {
            dbAdd(e.getKey(), e.getValue());
        }
        setLastSync(StructureResponse.class, now);
        conn.commit();
        LOG.info(String.format(Locale.ROOT, "imported %d structures", structures.size()));
    }

    private void dbAdd(long id, StructureResponse sr) throws IOException, SQLException {
        psAdd.setLong(1, id);
        psAdd.setInt(2, dbType(StructureResponse.class));
        psAdd.setString(3, sr.getName());
        psAdd.setString(4, encode(sr));
        psAdd.execute();
    }

    public StructureResponse getStructure(long structureId) throws IOException, SQLException, ClassNotFoundException {
        StructureResponse sr = decodeFirst(StructureResponse.class, structureId);
        if (sr == null) {
            sr = api.getStructure(structureId);
            dbAdd(structureId, sr);
            conn.commit();
        }
        return sr;
    }

    private void dbAdd(long id, CategoryResponse sr) throws IOException, SQLException {
        psAdd.setLong(1, id);
        psAdd.setInt(2, dbType(CategoryResponse.class));
        psAdd.setString(3, sr.getName());
        psAdd.setString(4, encode(sr));
        psAdd.execute();
    }

    public CategoryResponse getCategory(int categoryId) throws IOException, SQLException, ClassNotFoundException {
        CategoryResponse sr = decodeFirst(CategoryResponse.class, categoryId);
        if (sr == null) {
            sr = api.getCategory(categoryId);
            dbAdd(categoryId, sr);
            conn.commit();
        }
        return sr;
    }

    private void dbAdd(long id, MarketGroupResponse sr) throws IOException, SQLException {
        psAdd.setLong(1, id);
        psAdd.setInt(2, dbType(MarketGroupResponse.class));
        psAdd.setString(3, sr.getName());
        psAdd.setString(4, encode(sr));
        psAdd.execute();
    }

    public MarketGroupResponse getMarketGroup(int marketGroupId)
        throws IOException, SQLException, ClassNotFoundException {
        MarketGroupResponse sr = decodeFirst(MarketGroupResponse.class, marketGroupId);
        if (sr == null) {
            sr = api.getMarketGroup(marketGroupId);
            dbAdd(marketGroupId, sr);
            conn.commit();
        }
        return sr;
    }

    private <T> Map<Long, T> searchByName(Class<T> type, String namePart)
        throws ClassNotFoundException, SQLException, IOException {
        final String lcNamePart = namePart.toLowerCase(Locale.ROOT);
        return decodeAll(type, lcNamePart);
    }

    private void dbAdd(long id, SystemResponse sr) throws IOException, SQLException {
        psAdd.setLong(1, id);
        psAdd.setInt(2, dbType(SystemResponse.class));
        psAdd.setString(3, sr.getName());
        psAdd.setString(4, encode(sr));
        psAdd.execute();
    }

    public void loadSystems() throws IOException, ApiException, SQLException {
        if (getLastSync(SystemResponse.class) > 0) {
            return;
        }

        LOG.info("fetching systems data");
        long now = System.currentTimeMillis();
        Map<Integer, SystemResponse> systems = api.dumpSystems();
        for (Entry<Integer, SystemResponse> e : systems.entrySet()) {
            dbAdd((long) e.getKey(), e.getValue());
        }
        setLastSync(SystemResponse.class, now);
        conn.commit();
        LOG.info(String.format(Locale.ROOT, "imported %d systems", systems.size()));
    }

    public SystemResponse getSystem(int systemId) throws ClassNotFoundException, SQLException, IOException {
        return decodeFirst(SystemResponse.class, systemId);
    }

    public List<SystemResponse> searchSystemsByName(String namePart)
        throws ClassNotFoundException, SQLException, IOException {
        return new ArrayList<>(searchByName(SystemResponse.class, namePart).values());
    }

    public void loadStations() throws IOException, ApiException, SQLException, ClassNotFoundException {
        if (getLastSync(StationResponse.class) > 0) {
            return;
        }
        loadSystems();

        LOG.info("fetching stations data");
        long now = System.currentTimeMillis();
        Set<Integer> stationIds = new HashSet<>();
        Map<Long, SystemResponse> systems = searchByName(SystemResponse.class, "%");
        systems.forEach((id, sr) -> stationIds.addAll(sr.getStations()));
        systems = null;
        Map<Integer, StationResponse> stations = api.dumpStations(stationIds);
        for (Entry<Integer, StationResponse> e : stations.entrySet()) {
            psAdd.setLong(1, e.getValue().getStationId());
            psAdd.setInt(2, dbType(StationResponse.class));
            psAdd.setString(3, e.getValue().getName());
            psAdd.setString(4, encode(e.getValue()));
            psAdd.execute();
        }
        setLastSync(StationResponse.class, now);
        conn.commit();
        LOG.info(String.format(Locale.ROOT, "imported %d stations", stations.size()));
    }

    public StationResponse getStation(int id) throws ClassNotFoundException, SQLException, IOException {
        return decodeFirst(StationResponse.class, id);
    }

    public List<StationResponse> searchStationsByName(String namePart)
        throws ClassNotFoundException, SQLException, IOException {
        return new ArrayList<>(searchByName(StationResponse.class, namePart).values());
    }

    public Long toLocationId(String location) throws ApiException, ClassNotFoundException, SQLException, IOException {
        return toLocationId(location, false);
    }

    public Long toLocationId(String location, boolean dumpChoices)
        throws ApiException, ClassNotFoundException, SQLException, IOException {
        try {
            return Long.parseLong(location);
        } catch (NumberFormatException ex) {
        }
        Map<Long, Object> results = new HashMap<>();
        searchByName(StructureResponse.class, location).forEach((id, sr) -> results.put(id, sr));
        searchByName(StationResponse.class, location).forEach((id, sr) -> results.put(id, sr));
        searchByName(SystemResponse.class, location).forEach((id, sr) -> results.put(id, sr));

        if (dumpChoices && results.size() > 1) {
            for (Entry<Long, Object> entry : results.entrySet()) {
                if (entry.getValue() instanceof StationResponse) {
                    LOG.info(String.format("Station: %d - %s", entry.getKey().longValue(),
                        ((StationResponse) entry.getValue()).getName()));
                } else if (entry.getValue() instanceof SystemResponse) {
                    LOG.info(String.format("System: %d - %s", entry.getKey().longValue(),
                        ((StationResponse) entry.getValue()).getName()));
                } else if (entry.getValue() instanceof StructureResponse) {
                    LOG.info(String.format("Structure: %d - %s", entry.getKey().longValue(),
                        ((StationResponse) entry.getValue()).getName()));
                }
            }
        }
        return results.size() == 1 ? results.keySet().iterator().next().longValue() : null;
    }

    private int dbType(Class<?> type) {
        if (TypeResponse.class.isAssignableFrom(type)) {
            return 0;
        } else if (RegionResponse.class.isAssignableFrom(type)) {
            return 1;
        } else if (StructureResponse.class.isAssignableFrom(type)) {
            return 2;
        } else if (StationResponse.class.isAssignableFrom(type)) {
            return 3;
        } else if (SystemResponse.class.isAssignableFrom(type)) {
            return 4;
        } else if (ConstellationResponse.class.isAssignableFrom(type)) {
            return 5;
        } else if (CategoryResponse.class.isAssignableFrom(type)) {
            return 6;
        } else if (MarketGroupResponse.class.isAssignableFrom(type)) {
            return 7;
        }
        throw new RuntimeException("unmapped type: " + type.getName());
    }

    public void merge(List<MarketOrdersResponse> orders, Date timestamp) throws SQLException {
        // sort by on-disk order to speed up disk IO
        Collections.sort(orders, new Comparator<MarketOrdersResponse>() {

            @Override
            public int compare(MarketOrdersResponse o1, MarketOrdersResponse o2) {
                return o1.getOrderId().compareTo(o2.getOrderId());
            }
        });
        for (MarketOrdersResponse order : orders) {
            merge(order, timestamp);
        }
        closeOrders(orders, timestamp);
        conn.commit();
    }

    private void closeOrders(List<MarketOrdersResponse> orders, Date timestamp) throws SQLException {
        psMktHistGetOpenOrderIds.execute();
        List<Long> openDbOrders = new ArrayList<>();
        try (ResultSet rs = psMktHistGetOpenOrderIds.getResultSet()) {
            while (rs.next()) {
                openDbOrders.add(rs.getLong("ORDERID"));
            }
        }
        Set<Long> activeOrders = new HashSet<>();
        for (MarketOrdersResponse mor : orders) {
            activeOrders.add(mor.getOrderId());
        }
        int nClosed = 0;
        for (long orderId : openDbOrders) {
            if (!activeOrders.contains(orderId)) {
                psMktHistCloseOrder.setTimestamp(1, new Timestamp(timestamp.getTime()));
                psMktHistCloseOrder.setLong(2, orderId);
                assertEquals(1, psMktHistCloseOrder.executeUpdate());
                nClosed++;
            }
        }
        LOG.info("closed " + nClosed + " orders");
    }
    
    private void merge(MarketOrdersResponse order, Date timestamp) throws SQLException {
        final String encodedOrder = encode(order);
        final byte[] orderDigest = messageDigest.digest(encodedOrder.getBytes(StandardCharsets.UTF_8));
        
        psMktHistGetOrder.setLong(1, order.getOrderId());
        byte[] lastDigest = null;
        try (ResultSet rs = psMktHistGetOrder.executeQuery()) {
            while (rs.next()) {
                Timestamp closed = rs.getTimestamp("CLOSED");
                if (closed != null) {
                    LOG.warn("closed is not null");
                    ResultSetUtils.dump(rs);
                }
                lastDigest = rs.getBytes("DIGEST");
            }
        }

        // order not in history database yet?
        // or: order has changed?
        if (lastDigest == null || !Arrays.equals(orderDigest, lastDigest)) {
            psMktHistAdd.setLong(1, order.getOrderId());
            psMktHistAdd.setTimestamp(2, new Timestamp(order.getIssued().toInstant().toEpochMilli()));
            psMktHistAdd.setTimestamp(3, null);
            psMktHistAdd.setBytes(4, orderDigest);
            psMktHistAdd.executeUpdate();
            
            psMktHistAddPayload.setLong(1, order.getOrderId());
            psMktHistAddPayload.setTimestamp(2, new Timestamp(timestamp.getTime()));
            psMktHistAddPayload.setString(3, encodedOrder);
            psMktHistAddPayload.executeUpdate();
            
            if (LOG.isTraceEnabled()) {
                LOG.trace("order changed: " + order.getOrderId());
            }
            return;
        }
    }

}
