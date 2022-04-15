package casualconsistency;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ServerToServerReceiveProcess extends Thread {

    Socket serverSocket;
    ObjectInputStream ois;
    private ArrayList<MessageInfo> messageInfo;

    public ServerToServerReceiveProcess(Socket serverSocket, ArrayList<MessageInfo> messageInfo) throws IOException {
        System.out.println("Started ServerToServerReceiveProcess on port " + serverSocket.getPort() + " local " + serverSocket.getLocalPort());
        this.serverSocket = serverSocket;
        this.messageInfo = messageInfo;
        ois = new ObjectInputStream(serverSocket.getInputStream());
    }

    public void run() {
        while (true) {
            MessageInfo message = receiveMessages();
            if (message != null) {
                messageInfo.add(message);
                callPython(messageInfo);
            }
        }
    }

    private String convertToString(ArrayList<MessageInfo> messages){
        String str = ""; 
        for (MessageInfo info : messages) {
            str = str + info.timestamp + " \"" + info.message + "\" ";
        }
        return str;
    }

    private void callPython(ArrayList<MessageInfo> messages) {
        String command = "python hello.py " + convertToString(messages); // change to correct name

        System.out.println("Going to run " + command);
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            System.out.println(in.readLine());
        } catch (IOException e) {
            System.out.println("Error on Python: " + e);
        }
    }// callPython

    //@SuppressWarnings("unchecked")
    private MessageInfo receiveMessages() {
        MessageInfo message = null;
        try {
            System.out.println("Waiting for message from port: " + serverSocket.getPort());
            message = (MessageInfo) ois.readObject();
            System.out.println("Received message: " +  message);
            //messageInfo.addAll(messageInfoArray);
        } catch (ClassCastException e) {
            System.out.println("Error on casting to MessageInfo Obj: " + e);
        } catch (Exception e) {
            System.out.println("Error on command read: " + e);
        }
        return message;
    }

}
