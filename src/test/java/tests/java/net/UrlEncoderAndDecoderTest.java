package tests.java.net;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.net.URLDecoder;
import java.net.URLEncoder;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UrlEncoderAndDecoderTest {

    @Test
    public void test() throws Exception {
        String param1BeforeEncoding = "hello there";
        String param1AfterEncoding = URLEncoder.encode(param1BeforeEncoding, "UTF-8");
        String param1AfterDecoding = URLDecoder.decode(param1AfterEncoding, "UTF-8");
        assertEquals("hello+there", param1AfterEncoding);
        assertEquals(param1BeforeEncoding, param1AfterDecoding);

        String param2BeforeEncoding = "good-bye, friend";
        String param2AfterEncoding = URLEncoder.encode(param2BeforeEncoding, "UTF-8");
        String param2AfterDecoding = URLDecoder.decode(param2AfterEncoding, "UTF-8");
        assertEquals("good-bye%2C+friend", param2AfterEncoding);
        assertEquals(param2BeforeEncoding, param2AfterDecoding);
        
        assertEquals("a b c", URLDecoder.decode("a+b%20c", "UTF-8"));
    }

    /**
     * Uri uri=Uri.parse(url_string);
     * uri.getQueryParameter("para1");
     */
//    @Test
//    public void test2() {
//        MultiMap<String> params = new MultiMap<String>();
//        UrlEncoded.decodeTo("foo=bar&bla=blu%20b&a=1+2", params, "UTF-8");
//
//        assertEquals(params.getString("foo"), "bar");
//        assertEquals(params.getString("bla"), "blu b");
//        assertEquals(params.getString("a"), "1 2");
//    }
}