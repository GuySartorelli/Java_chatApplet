package messages;

import java.io.Serializable;

public class Message implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3990501493138505592L;
    private String sender;
    private String message;
    
    public Message (String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return sender + ": " + message;
    }
}
