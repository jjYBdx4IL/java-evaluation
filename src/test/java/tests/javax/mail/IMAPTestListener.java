package tests.javax.mail;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.mail.event.MailEvent;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.event.StoreEvent;
import javax.mail.event.StoreListener;


public class IMAPTestListener implements ConnectionListener, FolderListener, StoreListener,
        MessageChangedListener, MessageCountListener {

    public List<MailEvent> all = Collections.synchronizedList(new ArrayList<MailEvent>());
    public List<ConnectionEvent> opened = Collections.synchronizedList(new ArrayList<ConnectionEvent>());
    public List<ConnectionEvent> disconnected = Collections.synchronizedList(new ArrayList<ConnectionEvent>());
    public List<ConnectionEvent> closed = Collections.synchronizedList(new ArrayList<ConnectionEvent>());
    public List<FolderEvent> folderCreated = Collections.synchronizedList(new ArrayList<FolderEvent>());
    public List<FolderEvent> folderDeleted = Collections.synchronizedList(new ArrayList<FolderEvent>());
    public List<FolderEvent> folderRenamed = Collections.synchronizedList(new ArrayList<FolderEvent>());
    public List<StoreEvent> storeNotification = Collections.synchronizedList(new ArrayList<StoreEvent>());
    public List<MessageChangedEvent> msgChanged = Collections.synchronizedList(new ArrayList<MessageChangedEvent>());
    public List<MessageCountEvent> msgsAdded = Collections.synchronizedList(new ArrayList<MessageCountEvent>());
    public List<MessageCountEvent> msgsRemoved = Collections.synchronizedList(new ArrayList<MessageCountEvent>());

    @Override
    public void opened(ConnectionEvent e) {
        all.add(e);
        opened.add(e);
    }

    @Override
    public void disconnected(ConnectionEvent e) {
        all.add(e);
        disconnected.add(e);
    }

    @Override
    public void closed(ConnectionEvent e) {
        all.add(e);
        closed.add(e);
    }

    @Override
    public void folderCreated(FolderEvent e) {
        all.add(e);
        folderCreated.add(e);
    }

    @Override
    public void folderDeleted(FolderEvent e) {
        all.add(e);
        folderDeleted.add(e);
    }

    @Override
    public void folderRenamed(FolderEvent e) {
        all.add(e);
        folderRenamed.add(e);
    }

    @Override
    public void notification(StoreEvent e) {
        all.add(e);
        storeNotification.add(e);
    }

    @Override
    public void messageChanged(MessageChangedEvent e) {
        all.add(e);
        msgChanged.add(e);
    }

    @Override
    public void messagesAdded(MessageCountEvent e) {
        all.add(e);
        msgsAdded.add(e);
    }

    @Override
    public void messagesRemoved(MessageCountEvent e) {
        all.add(e);
        msgsRemoved.add(e);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("IMAPTestListener [");
        builder.append("all=");
        builder.append(all);
        builder.append(", closed=");
        builder.append(closed);
        builder.append(", disconnected=");
        builder.append(disconnected);
        builder.append(", folderCreated=");
        builder.append(folderCreated);
        builder.append(", folderDeleted=");
        builder.append(folderDeleted);
        builder.append(", folderRenamed=");
        builder.append(folderRenamed);
        builder.append(", msgChanged=");
        builder.append(msgChanged);
        builder.append(", msgsAdded=");
        builder.append(msgsAdded);
        builder.append(", msgsRemoved=");
        builder.append(msgsRemoved);
        builder.append(", opened=");
        builder.append(opened);
        builder.append(", storeNotification=");
        builder.append(storeNotification);
        builder.append("]");
        return builder.toString();
    }

}
