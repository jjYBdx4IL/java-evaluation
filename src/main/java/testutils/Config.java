package testutils;

import com.github.jjYBdx4IL.utils.env.Env;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class Config implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);
    private static final File CONFIG_FILE = Env.getConfigDir(TestAutorunGUI.class);
    
    public ControlWindow controlWindow = new ControlWindow();
    public MethodRef selectedTestMethod;
    
    public class ControlWindow implements Serializable {

        public Rectangle bounds;

    }
    
    public static void save(Config config) {
        if (!CONFIG_FILE.getParentFile().exists()) {
            CONFIG_FILE.getParentFile().mkdirs();
        }
        try (OutputStream os = new FileOutputStream(CONFIG_FILE)) {
            try (ObjectOutputStream oos = new ObjectOutputStream(os)) {
                oos.writeObject(config);
            }
        } catch (Exception ex) {
            LOG.error("", ex);
        }
    }
    
    public static Config load() {
        if (!CONFIG_FILE.exists()) {
            return new Config();
        }
        try (InputStream os = new FileInputStream(CONFIG_FILE)) {
            try (ObjectInputStream ois = new ObjectInputStream(os)) {
                try {
                    return (Config) ois.readObject();
                } catch (ClassNotFoundException ex) {
                    throw new IOException(ex);
                }
            }
        } catch (Exception ex) {
            LOG.error("", ex);
        }
        return new Config();
    }
    
    public void save() {
        save(this);
    }
}
