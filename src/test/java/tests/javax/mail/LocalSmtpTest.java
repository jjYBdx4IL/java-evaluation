package tests.javax.mail;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class LocalSmtpTest {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSmtpTest.class);

    @Ignore
    @Test
    public void test() throws Exception {
        sendSimpleMessage("subject", "content");
    }

    protected void sendSimpleMessage(String subject, String body) throws AddressException, MessagingException {
        Session session = getSmtpSession();
        MimeMessage msg = new MimeMessage(session);
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(System.getProperty("user.name") + "@localhost"));
        msg.setSubject(subject);
        Multipart mp = new MimeMultipart();
        BodyPart bp = new MimeBodyPart();
        bp.setText(body);
        mp.addBodyPart(bp);
        msg.setContent(mp);
        // set the message content here
        Transport t = session.getTransport();
        try {
            t.connect();
            t.sendMessage(msg, msg.getAllRecipients());
        } finally {
            t.close();
        }

    }

    protected Session getSmtpSession() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");

        Session session = Session.getDefaultInstance(props);
        LOG.debug("enable smtp debugging");
        session.setDebug(true);
        return session;
    }
}
