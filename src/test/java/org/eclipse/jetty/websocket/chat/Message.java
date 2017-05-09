package org.eclipse.jetty.websocket.chat;

/**
 *
 * @author jjYBdx4IL
 */
public class Message {

    private final String message;
    
    public Message(String message) {
        this.message = message;
    }
    
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

}
