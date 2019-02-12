package jhighlight;

import com.github.jjYBdx4IL.utils.io.FindUtils;
import com.github.jjYBdx4IL.utils.io.IoUtils;
import com.uwyn.jhighlight.renderer.XhtmlRendererFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class JHighLightTest {

    @Test
    public void test() throws FileNotFoundException, IOException {
        File src = FindUtils.globOneFile("**/ExampleToBeHighLighted.java");
        String ext = IoUtils.getExt(src);

        String result = XhtmlRendererFactory.getRenderer(ext)
            .highlight(src.getName(), FileUtils.readFileToString(src, "UTF-8"), "UTF-8", false);
        System.out.println(result);
    }
    
    @Test
    public void testFragment() throws FileNotFoundException, IOException {
        File src = FindUtils.globOneFile("**/ExampleToBeHighLighted.java");
        String ext = IoUtils.getExt(src);

        String result = XhtmlRendererFactory.getRenderer(ext)
            .highlight(src.getName(), FileUtils.readFileToString(src, "UTF-8"), "UTF-8", true);
        System.out.println(result);
    }
}
