package javacpp.opencv.eve;

import java.io.File;

public class Utils {

    public static String sharedCacheLoc = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Eve Online\\SharedCache";

    static {
        if (!new File(sharedCacheLoc).exists()) {
            sharedCacheLoc = System.getProperty("user.home") + "/.wine/drive_c/eve/SharedCache";
        }
    }
    
}
