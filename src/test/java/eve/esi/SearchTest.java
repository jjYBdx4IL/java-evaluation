package eve.esi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.api.SearchApi;
import net.troja.eve.esi.api.SsoApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.model.CharacterInfo;
import net.troja.eve.esi.model.CharacterSearchResponse;
import net.troja.eve.esi.model.SearchResponse;
import net.troja.eve.esi.model.StructureResponse;
import net.troja.eve.esi.model.UniverseIdsResponse;

public class SearchTest {

    private static final Logger LOG = LoggerFactory.getLogger(SearchTest.class);

    // only way to find player structures seems to be getting an id dump of all
    // structs and then pulling the dataset for each single one one by one.
    @Test
    public void testSearchForStructure() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());

        ApiClient apiClient = ApiClientFactory.getClient();

        CharacterInfo info = new SsoApi(apiClient).getCharacterInfo();
        UniverseApi uniApi = new UniverseApi(apiClient);
        List<Long> structs = uniApi.getUniverseStructures(null, null, null);
        LOG.info(String.format("%d structures", structs.size()));
        long someStructId = structs.get(0);
        StructureResponse structRes = uniApi.getUniverseStructuresStructureId(someStructId, null, null, null);
        LOG.info("searching for: " + structRes.getName());
        SearchApi searchApi = new SearchApi(apiClient);
        SearchResponse searchRes = searchApi.getSearch(Arrays.asList("station"), structRes.getName(), null, null, null,
            null, false);
        assertEquals(0, searchRes.getStation().size());
        CharacterSearchResponse charSearchRes = searchApi.getCharactersCharacterIdSearch(Arrays.asList("station"),
            info.getCharacterID(), structRes.getName(), null, null, null, null, false, null);
        assertEquals(0, charSearchRes.getStation().size());
        assertEquals(0, charSearchRes.getStructure().size());
        // LOG.info("" + searchRes.getStation().get(0));

        UniverseIdsResponse uir = uniApi.postUniverseIds(Arrays.asList(structRes.getName()), null, null, null);
        LOG.info("" + uir);

        StructureResponse sr = uniApi.getUniverseStructuresStructureId(someStructId, null, null, null);
        LOG.info("" + sr);

        LOG.info("" + apiClient.getJSON().serialize(sr));
        LOG.info("" + apiClient.getJSON().deserialize(apiClient.getJSON().serialize(sr), StructureResponse.class));
    }
}
