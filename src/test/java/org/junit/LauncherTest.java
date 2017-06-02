package org.junit;

import org.apache.tools.ant.DirectoryScannerTest;
import static org.junit.Assert.assertEquals;
import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

/**
 *
 * @author jjYBdx4IL
 */
public class LauncherTest {

    @Test
    public void test() {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectPackage("com.example.mytests"),
                        selectClass(DirectoryScannerTest.class)
                )
                .filters(includeClassNamePatterns(".*Test"))
                .build();

        Launcher launcher = LauncherFactory.create();

        // Register a listener of your choice
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);

        launcher.execute(request, new TestExecutionListener[0]);
        
        assertEquals(1, listener.getSummary().getTestsSucceededCount());
    }
}
