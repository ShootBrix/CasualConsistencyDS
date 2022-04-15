package casualconsistency;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageInfo implements Serializable {
    public static int messageID = 0;
    public String message;
    public LocalDateTime timestamp;
    public int portID;
    

    public MessageInfo(){
        messageID++;
    }

    @Override
    public boolean equals(Object otherObj) {
        MessageInfo other = (MessageInfo) otherObj;
        if (this.timestamp.equals( other.timestamp) && this.message.equals(other.message) && this.portID == other.portID) {
            return true;
        } else {
            return false;
        }
    }

    public String toString(){
        return "" + messageID + " " + timestamp + " " + message + " " + portID;
    }
}
