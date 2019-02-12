package org.apache.poi;

import com.github.jjYBdx4IL.utils.env.Maven;
import org.apache.poi.hmef.extractor.HMEFContentsExtractor;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TnefParserTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(TnefParserTest.class);
    
    @Test
    public void test() throws IOException {
        HMEFContentsExtractor ext = new HMEFContentsExtractor(new File("src/test/resources/winmail.dat"));
        
        File rtf = new File(TEMP_DIR, "message.rtf");
           
        System.out.println("Extracting...");
        ext.extractMessageBody(rtf);
        ext.extractAttachments(TEMP_DIR);
        System.out.println("Extraction completed");
    }
}
