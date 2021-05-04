package pushbullet;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Env;
import com.github.jjYBdx4IL.utils.env.Surefire;
import com.github.sheigutn.pushbullet.Pushbullet;

import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * https://github.com/Sheigutn/pushbullet-java-8
 * 
 * https://docs.pushbullet.com/#api-quick-start
 * 
 *
 */

public class PushbulletTest {

    @Test
    public void test() throws IOException {
        assumeTrue(Surefire.isSingleTestExecution());

        Pushbullet pushbullet = getClient();
        
        pushbullet.pushNote("simply a test", "This is just a simple test!");
    }
    
    public static Pushbullet getClient() throws IOException {
        Properties config = Env.readAppConfig(PushbulletTest.class);
        String apiToken = config.getProperty("apiToken");
        Pushbullet pushbullet = new Pushbullet(apiToken);
        return pushbullet;
    }
}
