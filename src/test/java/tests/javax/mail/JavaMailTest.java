package tests.javax.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.utils.logic.Condition;
import com.github.jjYBdx4IL.utils.security.PasswordGenerator;
import com.sun.mail.imap.IMAPFolder;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class JavaMailTest {

    private static final Logger log = Logger.getLogger(JavaMailTest.class.getName());

    @Ignore
    @Test
    public void testImapListeners() throws NoSuchProviderException, MessagingException, InterruptedException {
        Store store = getGmailImapStore();
        final IMAPTestListener imapTestListener = new IMAPTestListener();
        store.addConnectionListener(imapTestListener);
        store.addFolderListener(imapTestListener);
        store.addStoreListener(imapTestListener);
        store.connect();
        final IMAPFolder inbox = (IMAPFolder) store.getFolder("Inbox");
        inbox.addMessageChangedListener(imapTestListener);
        inbox.addMessageCountListener(imapTestListener);
        inbox.open(Folder.READ_ONLY);

        String tag = PasswordGenerator.generate55() + '.' + System.currentTimeMillis();
        sendSimpleMessage(tag + " 1", "test1");

        assertTrue(new Condition() {
            @Override
            public boolean test() {
                try {
                    inbox.idle(true);
                } catch (MessagingException ex) {
                    log.fatal(ex);
                }
                return imapTestListener.msgsAdded.size() == 1;
            }
        }.waitUntil());

        assertEquals("Inbox", ((Folder)imapTestListener.msgsAdded.get(0).getSource()).getFullName());

        log.info(imapTestListener.toString());
    }

    @SuppressWarnings("rawtypes")
	@Ignore
    @Test
    public void testImapStore() throws MessagingException, IOException {
        Store store = getGmailImapStore();
        store.connect();
        Folder inbox = store.getFolder("Inbox");
        inbox.open(Folder.READ_ONLY);
        Message messages[] = inbox.getMessages();
        for (Message message : messages) {
            MimeMessage mm = (MimeMessage) message;
            log.info(mm.getMessageID() + " " + message.getMessageNumber() + " " + message.getFolder() +
                    " " + mm.getSubject());
            for(Enumeration e = mm.getAllHeaderLines(); e.hasMoreElements();) {
                String s = (String) e.nextElement();
                log.info(s);
            }
            try(InputStream is = mm.getInputStream()) {
                log.info(IOUtils.toString(is, "ASCII"));
            }
        }

        for (Folder f : store.getPersonalNamespaces()) {
            log.info(f);
        }
        Folder defaultFolder = store.getDefaultFolder();
        log.info("default folder: " + defaultFolder.getFullName());
    }

    @Ignore
    @Test
    public void testSmtp() throws NoSuchProviderException, MessagingException {
        sendSimpleMessage("öäü", "test");
    }

    protected void sendSimpleMessage(String subject, String body) throws AddressException, MessagingException {
        Session session = getGmailSmtpSession();
        MimeMessage msg = new MimeMessage(session);
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TestCredentials.TEST_GMAIL_ADDRESS));
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

    protected Store getGmailImapStore() throws NoSuchProviderException {
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(TestCredentials.TEST_GMAIL_ADDRESS,
                        TestCredentials.TEST_GMAIL_PASSWORD);
            }
        };

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.debug", "true");
        props.put("mail.debug.auth", "true");
        props.put("mail.host", TestCredentials.TEST_GMAIL_IMAP_HOST);
        Session session = Session.getInstance(props, auth);
        Store store = session.getStore("imaps");
        //if (log.isDebugEnabled()) {
        log.debug("enable imap debugging");
        session.setDebug(true);
        //}

        return store;
    }

    protected Session getGmailSmtpSession() {
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(TestCredentials.TEST_GMAIL_ADDRESS,
                        TestCredentials.TEST_GMAIL_PASSWORD);
            }
        };

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.host", TestCredentials.TEST_GMAIL_SMTP_HOST);
        props.put("mail.debug.auth", "true");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.port", TestCredentials.TEST_GMAIL_SMTP_PORT);
        props.put("mail.smtps.socketFactory.port", TestCredentials.TEST_GMAIL_SMTP_PORT);
        props.put("mail.smtps.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtps.socketFactory.fallback", "false");
        props.setProperty("mail.smtps.quitwait", "false");

        props.put("mail.smtps.starttls.enable", "false");
        props.put("mail.smtps.starttls.required", "false");

        Session session = Session.getDefaultInstance(props, auth);
        //if (log.isDebugEnabled()) {
        log.debug("enable smtp debugging");
        session.setDebug(true);
        //}
        return session;
    }
}
