package tests.java.net;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.UnsupportedEncodingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.log4j.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class InternetAddressTest {

    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(InternetAddressTest.class.getName());

    @Test
    public void testInternetAddress() throws AddressException {
        assertEquals("asd", new InternetAddress("asd").toString());
        assertEquals("ASD", new InternetAddress("ASD").toString());
        assertEquals("a", new InternetAddress(" a ").toString());
        assertEquals("me  too <a@a.de>", new InternetAddress("  me  too  < a@a.de > ").toString());
        try {
            new InternetAddress("a@ö.de");
            fail();
        } catch(AddressException ex) {}
        try {
            new InternetAddress("ö@a.de");
            fail();
        } catch(AddressException ex) {}
    }

    @Test
    public void testGetAddress() throws AddressException {
        assertEquals("a@a.de", new InternetAddress("  me  too  < a@a.de > ").getAddress());
    }

    @Test
    public void testPersonalEncoding() throws UnsupportedEncodingException {
        InternetAddress ia = new InternetAddress();
        ia.setAddress("a@test.de");
        ia.setPersonal("öäü", "UTF-8");
        assertEquals("=?UTF-8?B?w7bDpMO8?= <a@test.de>", ia.toString());
        assertEquals("\"öäü\" <a@test.de>", ia.toUnicodeString());
    }

}
