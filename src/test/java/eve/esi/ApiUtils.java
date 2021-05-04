package eve.esi;

import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.HeaderUtil;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.MarketOrdersResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    public static void retryDelay(int secs) {
        try {
            Thread.sleep(secs * 1000L);
        } catch (InterruptedException e) {
            LOG.error("", e);
        }
    }

    public static interface MultiPageCallExecutor<T> {
        ApiResponse<List<T>> call(int page) throws ApiException;
    }

    public static <T> List<T> pageWalk(MultiPageCallExecutor<T> callExecutor) {
        return pageWalk(callExecutor, null);
    }

    /**
     * 
     * @param <T>
     *            the api response type
     * @param callExecutor
     *            the per-page call executor
     * @param prevLmod
     *            if set to the value returned by the last call, the method will
     *            return null if the lmod header date in the api response has not
     *            changed (or is newer). Otherwise, that argument will be
     *            updated to the newer lmod date of the returned result set.
     * @return the result set
     */
    public static <T> List<T> pageWalk(MultiPageCallExecutor<T> callExecutor, Date prevLmod) {
        int page = 1;
        List<T> results = new ArrayList<>();
        Date lmod = null;
        while (true) {
            try {
                ApiResponse<List<T>> response = callExecutor.call(page);
                results.addAll(response.getData());

                Date pageLmod = ApiUtils.getLmod(response);
//                LOG.info("lmod = " + pageLmod);
                
                // nothing updated yet?
                if (prevLmod != null && !pageLmod.after(prevLmod)) {
                    return null;
                }

                // make sure that all result pages come from the same result
                // set:
                if (lmod == null) {
                    lmod = pageLmod;
                }
                if (!lmod.equals(pageLmod)) {
                    LOG.info("result set changed during retrieval, restarting...");
                    LOG.info(lmod  + " vs " + pageLmod);
                    results.clear();
                    page = 1;
                    lmod = null;
                    retryDelay(90);
                    continue;
                }

                Integer xPages = HeaderUtil.getXPages(response.getHeaders());
                if (page >= xPages) {
                    if (prevLmod != null) {
                        prevLmod.setTime(lmod.getTime());
                    }
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
                if (e.getMessage().equals("Not Found")) {
                    throw new RuntimeException("element not found, id = " + id, e);
                }
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
        id /= 1000 * 1000;
        return id >= 60 && id < 70;
    }

    public static boolean isSystem(long id) {
        id /= 1000 * 1000;
        return id >= 30 && id < 31;
    }
    
    public static Date getLmod(ApiResponse<?> res) {
        String lmod = HeaderUtil.getHeader(res.getHeaders(), "last-modified");
        return new Date(ZonedDateTime.parse(lmod, HeaderUtil.DATE_FORMAT).toInstant().toEpochMilli());
    }
    
    public static boolean isNpcOrder(MarketOrdersResponse mor)  {
        return mor.getDuration() > 90;
    }
}
