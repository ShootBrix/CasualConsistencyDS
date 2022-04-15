package casualconsistency;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ServerToServerSendProcess extends Thread {
    Socket serverSocket;
    ObjectOutputStream oos;
    private ArrayList<MessageInfo> messageInfo;
    HashMap<Socket, Status> bChanged;

    public ServerToServerSendProcess(Socket serverSocket, ArrayList<MessageInfo> messageInfo, HashMap<Socket, Status> bChanged)
            throws IOException {
        System.out.println("Started ServerToServerSendProcess on port " + serverSocket.getPort() + " local " + serverSocket.getLocalPort());

        this.serverSocket = serverSocket;
        this.messageInfo = messageInfo;
        this.bChanged = bChanged;
        oos = new ObjectOutputStream(serverSocket.getOutputStream());
    }

    public void run() { //In threads run is implicitly invoked
        while (true) {
            sendMessage();
        }
    }

    // private String convertToString(ArrayList<MessageInfo> messages) {
    //     String str = "";
    //     for (MessageInfo info : messages) {
    //         str = str + info.timestamp + " " + info.message + " ";
    //     }
    //     return str;
    // }

    private void sendMessage() {
        // System.out.println("going to send synchronized message ... " +
        // messageInfo.toString());
        synchronized (bChanged) {
            Status status = bChanged.get(serverSocket);
            //System.out.println("Send: status is: " + status  + " on Port " + serverSocket.getPort());
            if (status != null && status.getChanged() == true) {
                try {
                    MessageInfo message = messageInfo.get( messageInfo.size() - 1);
                    System.out.println("sending ... " + message + " to port " + serverSocket.getPort());
                    oos.writeObject(message);
                    oos.flush();
                    oos.reset();
                    status.setChanged(false);
                } catch (IOException e) {
                    System.out.println("Error on sendMessage: " + e);
                }
            }
        }
    }

}
