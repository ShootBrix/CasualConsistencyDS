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
        System.out.println("Sending to port " + serverSocket.getPort() + " from my local " + serverSocket.getLocalPort());

        this.serverSocket = serverSocket;
        this.messageInfo = messageInfo;
        this.bChanged = bChanged;
        oos = new ObjectOutputStream(serverSocket.getOutputStream());
    }

    public void run() { //In threads run is implicitly invoked when object is created
        while (true) {
            sendMessage();
        }
    }

    private void sendMessage() {
        Status status = null;
        synchronized (bChanged) {
            status = bChanged.get(serverSocket);
        }
        if (status != null && status.getChanged() == true) {
            try {
                MessageInfo message = messageInfo.get( messageInfo.size() - 1);
                if(message.delayPortID == serverSocket.getPort()){
                    try {
                        System.out.print(".");
                        Thread.sleep( message.delayDuration * 1000); //delay to created inconsistency
                    } catch (InterruptedException e) {
                        System.out.println("Error on sleep: " + e);
                    } 
                }
                System.out.println("sending " + message + " to port " + serverSocket.getPort());
                oos.writeObject(message);
                oos.flush();
                oos.reset();
                status.setChanged(false);
            } catch (Exception e) {
                System.out.println("Error on sendMessage: " + e);
            }
        }
    }

}
