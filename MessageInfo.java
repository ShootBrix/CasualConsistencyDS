package casualconsistency;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageInfo implements Serializable {
    public int messageConsistencyID = 0;
    public String message;
    public LocalDateTime timestamp;
    public int delayPortID;
    public int delayDuration; // in sec
    

    public String toString(){
        return "" + messageConsistencyID + " " + timestamp + " " + message ;
    }
}
