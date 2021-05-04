package eve.esi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.github.jjYBdx4IL.utils.env.Env;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiClientBuilder;
import net.troja.eve.esi.auth.OAuth;
import net.troja.eve.esi.auth.SsoScopes;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * For this to work, you'll need a clientId specified in your local config file
 * ("clientId=..." - the path is shown when running this class without a config
 * file). You'll get that ID from https://developers.eveonline.com/ by
 * registering an application there (callback url http://localhost:61323/callback).
 *
 */
public class ApiClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ApiClientFactory.class);

    private ApiClientFactory() {
    }

    public static ApiClient getClient() throws Exception {
        Properties cfg = Env.readAppConfig("eve.esi");

        ApiClient apiClient;
        if (cfg.getProperty("refreshToken") != null) {
            apiClient = new ApiClientBuilder().clientID(cfg.getProperty("clientId"))
                .refreshToken(cfg.getProperty("refreshToken")).build();
        } else {
            apiClient = new ApiClientBuilder().clientID(cfg.getProperty("clientId")).build();

            Server server = new Server(61323);
            try {
                CallbackHandler handler = new CallbackHandler();
                server.setHandler(handler);
                server.start();

                OAuth auth = (OAuth) apiClient.getAuthentication("evesso");
                Set<String> scopes = SsoScopes.ALL;

                String authorizationUri = auth.getAuthorizationUri(
                    "http://localhost:61323/callback", scopes, handler.state);
                Desktop.getDesktop().browse(new URI(authorizationUri));

                handler.latch.await();

                auth.finishFlow(handler.code, handler.state);

                cfg.setProperty("refreshToken", auth.getRefreshToken());
                // cfg.setProperty("accessToken", auth.getAccessToken());
                Env.writeAppConfig("eve.esi", cfg);
            } finally {
                server.stop();
            }
        }

        return apiClient;
    }

    private static class CallbackHandler extends AbstractHandler {

        private CountDownLatch latch = new CountDownLatch(1);
        private String state = "x" + Math.abs(new Random().nextLong());
        private String code = null;

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
            LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

            if ("/callback".equals(target)) {
                code = request.getParameter("code");
                assertNotNull(code);
                assertEquals(state, request.getParameter("state"));

                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/plain");
                response.getWriter().print("Auth success. You may close this page now.");

                latch.countDown();
            }

            baseRequest.setHandled(true);
        }
    }
}
