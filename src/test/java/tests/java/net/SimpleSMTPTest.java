package tests.java.net;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.sun.mail.smtp.SMTPTransport;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.junit.Ignore;
import org.junit.Test;
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SimpleSMTPTest {

    /**
     * http://stackoverflow.com/questions/73580/how-do-i-send-an-smtp-message-from-java
     * @throws MessagingException
     */
    @Ignore
    @Test
    public void testSend() throws MessagingException {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.mGithub jjYBdx4IL Projects.de");
        props.put("mail.smtp.auth", "false");
        Session session = Session.getInstance(props, null);
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("web@mGithub jjYBdx4IL Projects.de"));;
        msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse("mark@mGithub jjYBdx4IL Projects.de", false));
        msg.setSubject("Heisann " + System.currentTimeMillis());
        msg.setText("Med vennlig hilsennTov Are Jacobsen");
        msg.setHeader("X-Mailer", "Tov Are's program");
        msg.setSentDate(new Date());
        SMTPTransport t
                = (SMTPTransport) session.getTransport("smtp");
        t.connect("smtp.mGithub jjYBdx4IL Projects.de", "web@mGithub jjYBdx4IL Projects.de", "<insert password here>");
        t.sendMessage(msg, msg.getAllRecipients());
        System.out.println("Response: " + t.getLastServerResponse());
        t.close();
    }
}
