package tests.java.nio.charset;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings("all")
public class CharsetDecoderTest {

    // unfinished -- may not make sense at all!
    @Ignore
    @Test
    public void testUTF8Decoder() throws IOException {
        
        CharsetDecoder decoder =
            Charset.forName("UTF8").newDecoder().onMalformedInput(CodingErrorAction.REPORT);
        URL url = CharsetDecoderTest.class.getResource("iso8859-1.txt");
        String s = IOUtils.toString(url, "UTF-8");
        byte[] b = s.getBytes(Charset.forName("UTF8"));
        String s2;
//        decoder.decode(new ByteBuffer(b));
        s2 = StringUtils.toString(b, "UTF8");
        System.out.println(s2);
    }
}
