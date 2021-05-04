package eve.esi;

import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;

import org.junit.Test;

import java.util.List;

import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.HeaderUtil;
import net.troja.eve.esi.api.UniverseApi;

public class UniverseTest {

    @Test
    public void testXPages() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());
        
        ApiClient apiClient = ApiClientFactory.getClient();

        UniverseApi uniApi = new UniverseApi(apiClient);
        ApiResponse<List<Integer>> response = uniApi.getUniverseRegionsWithHttpInfo(null, null);
        
        assertNull(HeaderUtil.getXPages(response.getHeaders()));
    }
}
