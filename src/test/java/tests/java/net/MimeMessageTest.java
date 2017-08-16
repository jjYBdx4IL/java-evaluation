package tests.java.net;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class MimeMessageTest {

    private static final Logger log = Logger.getLogger(MimeMessageTest.class.getName());

    @Test
    public void testMimeMessageConstruction() throws MessagingException, IOException {
        MimeMessage m = new MimeMessage((Session) null);
        m.addFrom(new Address[]{new InternetAddress("from@localhost")});
        m.addRecipients(Message.RecipientType.TO, new Address[]{
            new InternetAddress("to1@localhost"),
            new InternetAddress("<to2@localhost>"),
            new InternetAddress("some name <to3@localhost>")
        });
        
        m.setSubject("some astonishingly meaningless subject");
        m.setSentDate(new Date());

        BodyPart bp = new MimeBodyPart();
        bp.setText("test body");

        Multipart mp = new MimeMultipart();
        mp.addBodyPart(bp);

        m.setContent(mp);
        m.saveChanges();

        String msg;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            m.writeTo(baos);
            msg = baos.toString();
            log.info(msg);
        }

        assertTrue(msg.contains("Date: "));
        assertTrue(msg.contains("From: "));
        assertTrue(msg.contains("To: "));
        assertTrue(msg.contains("Message-ID: "));
        assertTrue(msg.contains("Subject: some astonishingly meaningless subject"));
        assertTrue(msg.contains("MIME-Version: 1.0"));
        assertTrue(msg.contains("Content-Type: multipart/mixed;"));
        assertTrue(msg.contains("test body"));
    }
}
