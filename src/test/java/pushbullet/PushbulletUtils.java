package pushbullet;

import com.github.jjYBdx4IL.utils.time.TimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PushbulletUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PushbulletUtils.class);

    private static Map<String, Long> lastSent = new HashMap<>();

    public static synchronized void sendMessage(String message) {
        Long last = lastSent.get(message);
        if (last != null && last > System.currentTimeMillis() - TimeUtils.durationToMillis("5m")) {
            return;
        }
        try {
            LOG.info(message);
            String callingClass = Thread.currentThread().getStackTrace()[2].getClassName();
            callingClass = callingClass.substring(callingClass.lastIndexOf(".") + 1);
            if (callingClass.indexOf("$") > 0) {
                callingClass = callingClass.substring(0, callingClass.indexOf("$"));
            }
            callingClass = callingClass.replaceFirst("Test$", "");
            PushbulletTest.getClient().pushNote(callingClass, message);
            lastSent.put(message, System.currentTimeMillis());
        } catch (IOException e) {
            LOG.error("", e);
        }
    }

}
