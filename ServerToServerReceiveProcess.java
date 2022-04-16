package casualconsistency;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ServerToServerReceiveProcess extends Thread {

    Socket serverSocket;
    ObjectInputStream ois;
    private ArrayList<MessageInfo> messageInfo;

    public ServerToServerReceiveProcess(Socket serverSocket, ArrayList<MessageInfo> messageInfo) throws IOException {
        System.out.println(
                "Receiving from port " + serverSocket.getPort() + " on my local " + serverSocket.getLocalPort());
        this.serverSocket = serverSocket;
        this.messageInfo = messageInfo;
        ois = new ObjectInputStream(serverSocket.getInputStream());
    }

    public void run() { //In threads run is implicitly invoked when object is created
        while (true) {
            MessageInfo message = receiveMessages();
            if (message != null) {
                messageInfo.add(message);
                consistencyCheck(messageInfo);
                Collections.sort(messageInfo, new Comparator<MessageInfo>() {
                    public int compare(MessageInfo o1, MessageInfo o2) {
                        if (o1.messageConsistencyID == o2.messageConsistencyID)
                            return 0;
                        return o1.messageConsistencyID < o2.messageConsistencyID ? -1 : 1;
                    }
                });
                System.out.println("Message List after sort: "+ convertToString(messageInfo));
            }
        }
    }

    private String convertToString(ArrayList<MessageInfo> messages) {
        String str = "";
        for (MessageInfo info : messages) {
            str = str + " \"" + info.messageConsistencyID + " " + info.message + "\" " + info.timestamp;
        }
        return str;
    }

    private void consistencyCheck(ArrayList<MessageInfo> messages) {
        System.out.println("Message List: " + convertToString(messages));
        int index = 0;
        for (MessageInfo message : messages) {
            if (message.messageConsistencyID != index) {
                System.out.println("Messages are inconsistent!");
                break;
            }
            index++;
        }
        System.out.println("Messages: " + convertToString(messages));
    }

    private MessageInfo receiveMessages() {
        MessageInfo message = null;
        try {
            System.out.println("Waiting for message from port: " + serverSocket.getPort());
            message = (MessageInfo) ois.readObject();
            System.out.println("Received message: " + message);
        } catch (ClassCastException e) {
            System.out.println("Error on casting to MessageInfo Obj: " + e);
        } catch (Exception e) {
            System.out.println("Error on command read: " + e);
        }
        return message;
    }

}
