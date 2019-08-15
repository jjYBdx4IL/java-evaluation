package eve.esi;

import com.github.jjYBdx4IL.utils.text.Eta;
import eve.esi.ApiUtils.CallExecutor;
import eve.esi.ApiUtils.CallExecutor2;
import eve.esi.ApiUtils.CallExecutorVoid;
import eve.esi.ApiUtils.MultiPageCallExecutor;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.JSON;
import net.troja.eve.esi.api.LocationApi;
import net.troja.eve.esi.api.MarketApi;
import net.troja.eve.esi.api.SsoApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.api.UserInterfaceApi;
import net.troja.eve.esi.api.WalletApi;
import net.troja.eve.esi.model.CategoryResponse;
import net.troja.eve.esi.model.CharacterInfo;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterShipResponse;
import net.troja.eve.esi.model.ConstellationResponse;
import net.troja.eve.esi.model.MarketGroupResponse;
import net.troja.eve.esi.model.MarketOrdersResponse;
import net.troja.eve.esi.model.RegionResponse;
import net.troja.eve.esi.model.StationResponse;
import net.troja.eve.esi.model.StructureResponse;
import net.troja.eve.esi.model.SystemResponse;
import net.troja.eve.esi.model.TypeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApiWrapper {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ApiWrapper.class);
    public static final String DATASOURCE = "tranquility";

    private final ApiClient apiClient;
    private final UniverseApi uniApi;
    private final LocationApi locApi;
    private final SsoApi ssoApi;
    private final WalletApi wallet;
    private final MarketApi market;
    private final UserInterfaceApi uiApi;
    private final JSON json;

    public ApiWrapper(ApiClient apiClient) {
        this.apiClient = apiClient;
        this.uniApi = new UniverseApi(apiClient);
        this.ssoApi = new SsoApi(apiClient);
        this.locApi = new LocationApi(apiClient);
        this.wallet = new WalletApi(apiClient);
        this.market = new MarketApi(apiClient);
        this.uiApi = new UserInterfaceApi(apiClient);
        this.json = apiClient.getJSON();
    }

    ApiClient unwrap() {
        return apiClient;
    }
    
    public String serialize(Object obj) {
        return json.serialize(obj);
    }
    
    public <T> T deserialize(String input, Class<T> type) {
        return json.deserialize(input, type);
    }

    CallExecutor2<Integer, TypeResponse> callExecGetType = new CallExecutor2<Integer, TypeResponse>() {
        @Override
        public TypeResponse call(Integer id) throws ApiException {
            return uniApi.getUniverseTypesTypeId(id, null, DATASOURCE, null, null);
        }
    };
    
    public Map<Integer, TypeResponse> dumpTypes() throws ApiException, IOException {
        List<Integer> typeIds = ApiUtils.pageWalk(new MultiPageCallExecutor<Integer>() {
            @Override
            public ApiResponse<List<Integer>> call(int page) throws ApiException {
                return uniApi.getUniverseTypesWithHttpInfo(DATASOURCE, null, page);
            }
        });
        Map<Integer, TypeResponse> types = new HashMap<>();
        Eta<Integer> eta = new Eta<>(typeIds.size());
        int count = 0;
        for (int id : typeIds) {
            types.put(id, ApiUtils.retryWrap(callExecGetType, id));
            String etaStr = eta.toStringPeriodical(++count);
            if (etaStr != null) {
                LOG.info(etaStr);
            }
        }
        return types;
    }

    public TypeResponse getType(int id) {
        return ApiUtils.retryWrap(callExecGetType, id);
    }
    
    public Map<Integer, RegionResponse> dumpRegions() throws IOException, ApiException {
        return ApiUtils.idWalk(new CallExecutor<List<Integer>>() {
            @Override
            public List<Integer> call() throws ApiException {
                return uniApi.getUniverseRegions(DATASOURCE, null);
            }
        }, new CallExecutor2<Integer, RegionResponse>() {
            @Override
            public RegionResponse call(Integer id) throws ApiException {
                return uniApi.getUniverseRegionsRegionId(id, null, DATASOURCE, null, null);
            }
        });
    }

    CallExecutor2<Long, StructureResponse> callExecGetStructure = new CallExecutor2<Long, StructureResponse>() {
        @Override
        public StructureResponse call(Long id) throws ApiException {
            return uniApi.getUniverseStructuresStructureId(id, DATASOURCE, null, null);
        }
    };
    
    public Map<Long, StructureResponse> dumpStructures() throws IOException, ApiException {
        return ApiUtils.idWalk(new CallExecutor<List<Long>>() {
            @Override
            public List<Long> call() throws ApiException {
                return uniApi.getUniverseStructures(DATASOURCE, null, null);
            }
        }, callExecGetStructure);
    }

    public StructureResponse getStructure(long id) {
        return ApiUtils.retryWrap(callExecGetStructure, id);
    }
    
    CallExecutor2<Integer, CategoryResponse> callExecGetCategory = new CallExecutor2<Integer, CategoryResponse>() {
        @Override
        public CategoryResponse call(Integer id) throws ApiException {
            return uniApi.getUniverseCategoriesCategoryId(id, null, DATASOURCE, null, null);
        }
    };
    
    public CategoryResponse getCategory(int id) {
        return ApiUtils.retryWrap(callExecGetCategory, id);
    }

    CallExecutor2<Integer, MarketGroupResponse> callExecGetMarketGroup = new CallExecutor2<Integer, MarketGroupResponse>() {
        @Override
        public MarketGroupResponse call(Integer id) throws ApiException {
            return market.getMarketsGroupsMarketGroupId(id, null, DATASOURCE, null, null);
        }
    };
    
    public MarketGroupResponse getMarketGroup(int id) {
        return ApiUtils.retryWrap(callExecGetMarketGroup, id);
    }
    
    public Map<Integer, SystemResponse> dumpSystems() throws IOException, ApiException {
        return ApiUtils.idWalk(new CallExecutor<List<Integer>>() {
            @Override
            public List<Integer> call() throws ApiException {
                return uniApi.getUniverseSystems(DATASOURCE, null);
            }
        }, new CallExecutor2<Integer, SystemResponse>() {
            @Override
            public SystemResponse call(Integer id) throws ApiException {
                return uniApi.getUniverseSystemsSystemId(id, DATASOURCE, null, null, null);
            }
        });
    }

    public Map<Integer, ConstellationResponse> dumpConstellations() throws IOException, ApiException {
        return ApiUtils.idWalk(new CallExecutor<List<Integer>>() {
            @Override
            public List<Integer> call() throws ApiException {
                return uniApi.getUniverseConstellations(DATASOURCE, null);
            }
        }, new CallExecutor2<Integer, ConstellationResponse>() {
            @Override
            public ConstellationResponse call(Integer id) throws ApiException {
                return uniApi.getUniverseConstellationsConstellationId(id, DATASOURCE, null, null, null);
            }
        });
    }

    CallExecutor2<Integer, StationResponse> callExecGetStation = new CallExecutor2<Integer, StationResponse>() {
        @Override
        public StationResponse call(Integer id) throws ApiException {
            return uniApi.getUniverseStationsStationId(id, DATASOURCE, null);
        }
    }; 
    
    public Map<Integer, StationResponse> dumpStations(Set<Integer> stationIds) throws IOException, ApiException {
        return ApiUtils.idWalk(new CallExecutor<List<Integer>>() {
            @Override
            public List<Integer> call() throws ApiException {
                return new ArrayList<>(stationIds);
            }
        }, callExecGetStation);
    }
    
    public StationResponse getStation(int id) {
        return ApiUtils.retryWrap(callExecGetStation, id);
    }

    private CharacterInfo characterInfo = null;

    public CharacterInfo getCharInfo() throws ApiException {
        if (characterInfo == null) {
            characterInfo = ApiUtils.retryWrap(new CallExecutor<CharacterInfo>() {

                @Override
                public CharacterInfo call() throws ApiException {
                    return ssoApi.getCharacterInfo();
                }
            });
        }
        return characterInfo;
    }

    public CharacterShipResponse getActiveShip() throws ApiException {
        final CharacterInfo info = getCharInfo();
        return ApiUtils.retryWrap(new CallExecutor<CharacterShipResponse>() {

            @Override
            public CharacterShipResponse call() throws ApiException {
                return locApi.getCharactersCharacterIdShip(info.getCharacterID(), DATASOURCE, null, null);
            }
        });
    }

    public double getCashBalance() throws ApiException {
        CharacterInfo info = getCharInfo();
        return ApiUtils.retryWrap(new CallExecutor<Double>() {

            @Override
            public Double call() throws ApiException {
                return wallet.getCharactersCharacterIdWallet(info.getCharacterID(), DATASOURCE, null, null);
            }
        });
    }

    public CharacterLocationResponse getCurrentLocation() throws ApiException {
        CharacterInfo info = getCharInfo();
        return ApiUtils.retryWrap(new CallExecutor<CharacterLocationResponse>() {

            @Override
            public CharacterLocationResponse call() throws ApiException {
                return locApi.getCharactersCharacterIdLocation(info.getCharacterID(), DATASOURCE, null, null);
            }
        });
    }

    public long getCurrentLocationId() throws ApiException {
        return ApiUtils.toGenericLocationId(getCurrentLocation());
    }

    public List<MarketOrdersResponse> dumpMarketOrders(String orderType, int regionId) {
        return dumpMarketOrders(orderType, regionId, null);
    }
    
    /**
     *
     * @param prevLmod
     *            if set to the value returned by the last call, the method will
     *            return null if the lmod header date in the api response has not
     *            changed (or is newer). Otherwise, that argument will be
     *            updated to the newer lmod date of the returned result set.
     * @return the result set
     */
    public List<MarketOrdersResponse> dumpMarketOrders(String orderType, int regionId, Date prevLmod) {
        return ApiUtils.pageWalk(new MultiPageCallExecutor<MarketOrdersResponse>() {
            @Override
            public ApiResponse<List<MarketOrdersResponse>> call(int page) throws ApiException {
                return market.getMarketsRegionIdOrdersWithHttpInfo(orderType, regionId, DATASOURCE, null, page, null);
            }
        }, prevLmod);
    }

    public void setAutopilot(long destLocationId) {
        ApiUtils.retryWrap(new CallExecutorVoid() {

            @Override
            public void call() throws ApiException {
                uiApi.postUiAutopilotWaypoint(false, true, destLocationId, null, null);
            }
        });
    }

}
