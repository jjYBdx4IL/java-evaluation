package tests.javax.ws.rs;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

public class RestApp extends ResourceConfig {

    @Inject
    public RestApp(final ServiceLocator serviceLocator, final ServletContext context) {
        setApplicationName(RestApp.class.getSimpleName());

        // preload the LogicGraphClassifier to bootstrap the database
        // serviceLocator.getService(LogicGraphClassifier.class);

        // Register Features allowing for Multipart file uploads
        // register(MultiPartFeature.class);
        // register(JacksonFeature.class);

        // Enable Tracing support.
        property(ServerProperties.TRACING, "ALL");

        register(new AbstractBinder() {
            @Override
            public void configure() {
                bind(MyInterceptionService.class)
                    .to(org.glassfish.hk2.api.InterceptionService.class)
                    .in(Singleton.class);
                // bindFactory(EmfFactory.class).to(EntityManagerFactory.class).in(Singleton.class);
                // bindFactory(EmFactory.class).to(EntityManager.class).in(RequestScoped.class);
                // bindFactory(SessionDataFactory.class).to(SessionData.class).in(RequestScoped.class);
                // bind(Permissions.class).to(Permissions.class).in(RequestScoped.class);
            }
        });
        packages(true, getClass().getPackage().getName());
    }

}