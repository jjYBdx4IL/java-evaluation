package testutils;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Provides a persistent GUI that re-executes a selected test-method upon class change (ie. in combination with an IDE's
 * compile-on-save feature).
 *
 * Run this from the command line with "mvn exec:java". This requires all test dependencies having *compile* scope in
 * pom.xml because of the way we are re-loading the actual test classes: we need to prevent having the test classes
 * on the TestAutorunGUI's classpath because its class loaders will be used as parent for the temporary class loader
 * used to scan the test units and used to execute them.
 * 
 * @author jjYBdx4IL
 */
@SuppressWarnings("serial")
public class TestAutorunGUI extends JFrame implements ActionListener, Runnable, ComponentListener, ListSelectionListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestAutorunGUI.class);

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    new TestAutorunGUI().init();
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public TestAutorunGUI() {
        super(TestAutorunGUI.class.getSimpleName());
    }

    private void init() {
        rescanButton.addActionListener(this);
        selectedLatestButton.addActionListener(this);
        testMethodSelectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        testMethodSelectionList.addListSelectionListener(this);
        enableInputs(false);
        
        rescanButton.setToolTipText("scan classpath for @org.junit.Test annotations");
        selectedLatestButton.setToolTipText("select the test with the most recently updated class file");

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        Container container = getContentPane();
        setLayout(layout);

        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        container.add(statusLabel, c);

        c.gridx++;
        container.add(selectedLatestButton, c);

        c.gridx++;
        container.add(rescanButton, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        c.weighty = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        container.add(new JScrollPane(testMethodSelectionList), c);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        if (config.controlWindow.bounds != null) {
            setLocation(config.controlWindow.bounds.getLocation());
            setSize(config.controlWindow.bounds.getSize());
        } else {
            AWTUtils.centerOnMouseScreen(this);
        }
        addComponentListener(this);
        setVisible(true);

        Thread testMethodExecutorThread = new Thread(testMethodExecutor, "TestMethodExecutor");
        testMethodExecutorThread.start();

        doScan();
    }

    // scan classpath in separate thread
    @Override
    public void run() {
        try {
            List<MethodRef> testMethods = findAnnotatedTestMethodsInCurrentModuleOnly();
            Collections.sort(testMethods, new Comparator<MethodRef>() {
                @Override
                public int compare(MethodRef o1, MethodRef o2) {
                    return o1.toString().compareToIgnoreCase(o2.toString());
                }
            });
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    LOG.info("reloading list model with scan results");
                    testMethodSelectionList.removeListSelectionListener(TestAutorunGUI.this);
                    listModel.removeAllElements();
                    for (MethodRef method : testMethods) {
                        listModel.addElement(method);
                    }
                    enableInputs(true);
                    testMethodSelectionList.setSelectedValue(config.selectedTestMethod, true);
                    testMethodExecutor.setTestMethodRef(config.selectedTestMethod);
                    testMethodSelectionList.addListSelectionListener(TestAutorunGUI.this);
                }
            });
        } catch (Throwable ex) {
            LOG.error("", ex);
        }
    }

    /**
     *
     * @return test method -> full resource location URI of the class
     * @throws Throwable
     */
    public List<MethodRef> findAnnotatedTestMethodsInCurrentModuleOnly() throws Throwable {
        ClassLoader cl = new URLClassLoader(new URL[]{new URL(moduleUriPrefix)},
            Thread.currentThread().getContextClassLoader());
        
        final List<MethodRef> foundMethods = new ArrayList<>();

        try (ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .enableMethodInfo()
            .overrideClassLoaders(cl)
            .disableJarScanning()
            .scan()) {
            scanResult.getClassesWithMethodAnnotation(Test.class.getName())
                .forEach(ci -> {
                    try {
                        for (MethodInfo mi : ci.getMethodInfo()) {
                            if (mi.getAnnotationInfo(Test.class.getName()) != null) {
                                foundMethods.add(new MethodRef(ci.loadClass(), mi.loadClassAndGetMethod()));
                            }
                        }
                    } catch (NoClassDefFoundError err) {
                        LOG.error(err.getMessage());
                    }
                });
        }        
        
        return foundMethods;
    }

    public static String getResourceUri(Class<?> classRef) {
        ClassLoader cl = classRef.getClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        String classResourceFileName = classRef.getName().replace('.', '/') + ".class";
        return cl.getResource(classResourceFileName).toString();
    }

    private final JLabel statusLabel = new JLabel();
    private final JButton selectedLatestButton = new JButton("latest");
    private final JButton rescanButton = new JButton("rescan");
    private final DefaultListModel<MethodRef> listModel = new DefaultListModel<>();
    private final JList<MethodRef> testMethodSelectionList = new JList<>(listModel);

    private final Config config = Config.load();

    private final String moduleUriPrefix = Maven.getBasedir(TestAutorunGUI.class) + "target/test-classes/";
    private final TestMethodExecutor testMethodExecutor = new TestMethodExecutor(moduleUriPrefix);

    private void enableInputs(boolean enabled) {
        rescanButton.setEnabled(enabled);
        testMethodSelectionList.setEnabled(enabled);
        selectedLatestButton.setEnabled(enabled);
        statusLabel.setText(enabled ? "Ready." : "Scanning...");
    }

    private void doScan() {
        enableInputs(false);
        Thread t = new Thread(this, "ClasspathScanner");
        t.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info(e.getSource().toString());
        if (e.getSource() == rescanButton) {
            doScan();
        } else if (e.getSource() == selectedLatestButton) {
            selectLatestTest();
        }
    }

    protected void selectLatestTest() {
        long latestLmod = 0;
        MethodRef latestMethod = null;
        for (int i = 0; i < listModel.size(); i++) {
            try {
                MethodRef ref = listModel.get(i);
                File classFile = new File(new URI(ref.getResourceUri()));
                if (classFile.lastModified() > latestLmod) {
                    latestMethod = ref;
                    latestLmod = classFile.lastModified();
                }
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (latestMethod != null) {
            testMethodSelectionList.setSelectedValue(latestMethod, true);
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        config.controlWindow.bounds = e.getComponent().getBounds();
        config.save();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        config.controlWindow.bounds = e.getComponent().getBounds();
        config.save();
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        config.selectedTestMethod = testMethodSelectionList.getSelectedValue();
        testMethodExecutor.setTestMethodRef(config.selectedTestMethod);
        config.save();
    }
}
