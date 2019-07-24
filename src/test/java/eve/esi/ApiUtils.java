package eve.esi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.HeaderUtil;
import net.troja.eve.esi.model.CharacterLocationResponse;

public class ApiUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ApiUtils.class);
    public static final long RETRY_DELAY_MS = 5000;

    private ApiUtils() {

    }

    public static void retryDelay() {
        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException e) {
            LOG.error("", e);
        }
    }

    public static interface MultiPageCallExecutor<T> {
        ApiResponse<List<T>> call(int page) throws ApiException;
    }

    public static <T> List<T> pageWalk(MultiPageCallExecutor<T> callExecutor) {
        int page = 1;
        List<T> results = new ArrayList<>();
        while (true) {
            try {
                ApiResponse<List<T>> response = callExecutor.call(page);
                results.addAll(response.getData());
                Integer xPages = HeaderUtil.getXPages(response.getHeaders());
                if (page >= xPages) {
                    return results;
                }
                page++;
            } catch (ApiException e) {
                LOG.error("", e);
                retryDelay();
            }
        }
    }

    public static <T, U> Map<T, U> idWalk(CallExecutor<List<T>> idGetter, CallExecutor2<T, U> valueGetter)
        throws IOException, ApiException {
        Map<T, U> result = new HashMap<>();
        List<T> ids = ApiUtils.retryWrap(idGetter);
        for (T id : ids) {
            U response = ApiUtils.retryWrap(valueGetter, id);
            result.put(id, response);
        }
        return result;
    }

    public static interface CallExecutorVoid {
        void call() throws ApiException;
    }
    
    public static interface CallExecutor<T> {
        T call() throws ApiException;
    }

    public static interface CallExecutor2<T, U> {
        U call(T id) throws ApiException;
    }

    public static <T> T retryWrap(CallExecutor<T> callExecutor) {
        while (true) {
            try {
                return callExecutor.call();
            } catch (ApiException e) {
                LOG.error("", e);
                retryDelay();
            }
        }
    }

    public static <T, U> U retryWrap(CallExecutor2<T, U> callExecutor, T id) {
        while (true) {
            try {
                return callExecutor.call(id);
            } catch (ApiException e) {
                LOG.error("", e);
                retryDelay();
            }
        }
    }

    public static void retryWrap(CallExecutorVoid callExecutor) {
        while (true) {
            try {
                callExecutor.call();
                return;
            } catch (ApiException e) {
                LOG.error("", e);
                retryDelay();
            }
        }
    }
    
    public static long toGenericLocationId(CharacterLocationResponse clr) {
        if (clr.getStationId() != null) {
            return (long) clr.getStationId();
        }
        if (clr.getStructureId() != null) {
            return (long) clr.getStructureId();
        }
        return clr.getSolarSystemId();
    }

    // https://docs.esi.evetech.net/docs/id_ranges.html
    public static boolean isStation(long id) {
        id /= 1000*1000;
        return id >= 60 && id < 70;
    }
    public static boolean isSystem(long id) {
        id /= 1000*1000;
        return id >= 30 && id < 31;
    }
}
