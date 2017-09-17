package tests.javax.activation;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

public class MimetypesFileTypeMapTest {

    @Test
    public void test() throws IOException {
        MimetypesFileTypeMap map = new MimetypesFileTypeMap(getClass().getResourceAsStream("/META-INF/mimetypes.default"));
        FileTypeMap def = MimetypesFileTypeMap.getDefaultFileTypeMap();
        assertEquals("application/octet-stream", def.getContentType("asd.png"));
        assertEquals("image/jpeg", def.getContentType("asd.jpg"));
        assertEquals("image/jpeg", def.getContentType("asd.jpeg"));
        assertEquals("image/jpeg", def.getContentType("asd.JPG"));
        assertEquals("application/octet-stream", def.getContentType("asd.css"));
    }
}
