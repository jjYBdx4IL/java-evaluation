package eve.esi;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.api.LocationApi;
import net.troja.eve.esi.api.SearchApi;
import net.troja.eve.esi.api.SsoApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.api.UserInterfaceApi;
import net.troja.eve.esi.model.CharacterInfo;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.ConstellationResponse;
import net.troja.eve.esi.model.RegionResponse;
import net.troja.eve.esi.model.SearchResponse;
import net.troja.eve.esi.model.StationResponse;
import net.troja.eve.esi.model.StructureResponse;
import net.troja.eve.esi.model.SystemResponse;

public class RouteTest {

    private static final Logger LOG = LoggerFactory.getLogger(RouteTest.class);
    
    @Test
    public void testSetWaypoint() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());

        ApiClient apiClient = ApiClientFactory.getClient();

        SearchApi searchApi = new SearchApi(apiClient);
        SearchResponse sr = searchApi.getSearch(Arrays.asList("station"),
            "Jita IV - Moon 4 - Caldari Navy Assembly Plant", null, null, null, null, true);
        int jitaId = sr.getStation().get(0);
        sr = searchApi.getSearch(Arrays.asList("station"), "Dodixie IX - Moon 20 - Federation Navy Assembly Plant",
            null, null, null, null, true);
        int dodId = sr.getStation().get(0);
        UserInterfaceApi uiApi = new UserInterfaceApi(apiClient);
        uiApi.postUiAutopilotWaypoint(false, false, (long) jitaId, null, null);
        uiApi.postUiAutopilotWaypoint(false, false, (long) dodId, null, null);
    }

    @Test
    public void testGetCurrentLocation() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());
        
        ApiClient apiClient = ApiClientFactory.getClient();
        SsoApi ssoApi = new SsoApi(apiClient);
        CharacterInfo info = ssoApi.getCharacterInfo();
        LocationApi locApi = new LocationApi(apiClient);
        CharacterLocationResponse clr = locApi.getCharactersCharacterIdLocation(info.getCharacterID(), null, null, null);
        LOG.info("current station: " + clr.getStationId());
        LOG.info("current structure: " + clr.getStructureId());
        LOG.info("current solar system: " + clr.getSolarSystemId());
        UniverseApi uniApi = new UniverseApi(apiClient);
        String localName;
        int solarSysId;
        int constellationId = -1;
        if (clr.getStationId() != null) {
            StationResponse sr = uniApi.getUniverseStationsStationId(clr.getStationId(), null, null);
            localName = sr.getName();
            solarSysId = sr.getSystemId();
        }
        else if (clr.getStructureId() != null) {
            StructureResponse sr = uniApi.getUniverseStructuresStructureId(clr.getStructureId(), null, null, null);
            localName = sr.getName();
            solarSysId = sr.getSolarSystemId();
        }
        else {
            SystemResponse sr = uniApi.getUniverseSystemsSystemId(clr.getSolarSystemId(), null, null, null, null);
            localName = sr.getName();
            solarSysId = sr.getSystemId();
            constellationId = sr.getConstellationId();
        }
        if (constellationId == -1) {
            SystemResponse sr = uniApi.getUniverseSystemsSystemId(solarSysId, null, null, null, null);
            constellationId = sr.getConstellationId();
        }
        ConstellationResponse cr = uniApi.getUniverseConstellationsConstellationId(constellationId, null, null, null, null);
        RegionResponse rr = uniApi.getUniverseRegionsRegionId(cr.getRegionId(), null, null, null, null);
        String name = String.format("%s > %s > %s", rr.getName(), cr.getName(), localName);
        LOG.info("Your current location: " + name);
    }
}
