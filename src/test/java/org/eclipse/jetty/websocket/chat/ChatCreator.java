package org.eclipse.jetty.websocket.chat;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class ChatCreator implements WebSocketCreator
{
    private final ChatServer chatServer;

    public ChatCreator(ChatServer chatServer)
    {
        this.chatServer = chatServer;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest request, 
            ServletUpgradeResponse response)
    {
        // We want to create the Chat Socket and associate
        // it with our chatroom implementation
        return new ServerChatSocket(chatServer);
    }

}