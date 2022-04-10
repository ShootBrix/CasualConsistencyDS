
package casualconsistency;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {

    ServerSocket serverMainSocket = null;
    Socket clientSocket = null;
    Socket serverSocket = null;
    // array for messagesInfo
    public static ArrayList<MessageInfo> messageInfo = new ArrayList<MessageInfo>();

    public Server() throws IOException {
        System.out.println("Enter a port number for the client and server connection:");

        BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in));
        String choice = buffRead.readLine();
        int number = Integer.parseInt(choice);

        serverMainSocket = new ServerSocket(number);// port for client connections
        System.out.println("Server has started on port: " + serverMainSocket.getLocalPort());

    }

    public void run() throws IOException {

        connectToOtherServer();

        waitingForClient();

        new ServerToServerConnections(serverMainSocket, messageInfo).start();

        // wait for messages from other servers
        if (serverSocket != null) {
            ObjectInputStream ois = new ObjectInputStream(serverSocket.getInputStream());
            while (true) {
                System.out.println("Waiting for a message ");
                ArrayList<MessageInfo> message = receiveMessages(ois);
                callPython(message);
            }
        }
    }

    private ArrayList<MessageInfo> receiveMessages(ObjectInputStream ois) {
        ArrayList<MessageInfo> messageInfoArray = null;
        try {
            messageInfoArray = (ArrayList<MessageInfo>) ois.readObject();
        } catch (ClassCastException e) {
            System.out.println("Error on casting to MessageInfo Obj: " + e);
        } catch (Exception e) {
            System.out.println("Error on command read: " + e);
        }
        return messageInfoArray;
    }

    private void callPython(ArrayList<MessageInfo> messages) {
        String command = "python hello.py "; // change to correct name
        for (MessageInfo info : messages) {
            command = command + info.timestamp + " " + info.message + " ";
        }
        
        System.out.println(command);
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            System.out.println(in.readLine());
        } catch (IOException e) {
            System.out.println("Error on Python: " + e);
        }
    }

    private void connectToOtherServer() {
        while (true) {
            System.out.println("Enter a port number for connection to other existing server OR '0' to continue:");
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in));
            try {
                String choice;
                choice = buffRead.readLine();
                int number = Integer.parseInt(choice);
                if (number == 0)
                    break;

                serverSocket = new Socket("localhost", number);
            } catch (IOException e) {
                System.out.println("Error on connection to serverSocket: " + e);
            }
        }
    }

    private void waitingForClient() {
        System.out.println("Waiting for connections from Client...");
        try {
            clientSocket = serverMainSocket.accept();
            new ServerClientProcess(clientSocket, messageInfo).start();
        } catch (IOException e) {
            System.out.println("Connection error from Client: " + e);
        } catch (SecurityException e) {
            System.out.println("Security error: " + e);
        }
    }
}

class ServerToServerConnections extends Thread { // Thread for new Servers connections

    ServerSocket serverMainSocket = null;
    private ArrayList<MessageInfo> messageInfo;
    // array of servers connected to this server
    Socket newServerSocket = null;

    public ServerToServerConnections(ServerSocket serverSocket, ArrayList<MessageInfo> messageInfo) {
        System.out.println("Started ServerToServerConnections on port " + serverSocket.getLocalPort());
        this.serverMainSocket = serverSocket;
        this.messageInfo = messageInfo;
    }

    public void run() {
        while (true) {
            System.out.println("Waiting for connections from Servers...");
           
            try {
                newServerSocket = serverMainSocket.accept();
                new ServerToServerProcess(newServerSocket, messageInfo).start();
            } catch (IOException e) {
                System.out.println("Connection error from Server: " + e);
            } catch (SecurityException e) {
                System.out.println("Security error: " + e);
            }
        }
    }
}

class ServerToServerProcess extends Thread { // thread for Server to Server communication

    Socket serverSocket;
    ObjectOutputStream oos;
    private ArrayList<MessageInfo> messageInfo;

    public ServerToServerProcess(Socket serverSocket, ArrayList<MessageInfo> messageInfo) throws IOException {
        System.out.println("Started ServerToServerProcess on port " + serverSocket.getLocalPort());
        this.serverSocket = serverSocket;
        this.messageInfo = messageInfo;
        oos = new ObjectOutputStream(serverSocket.getOutputStream());
    }

    public void run() {
        while (true) {
            sendMessage();
        }
    }

    private void sendMessage() {
        if (messageInfo.isEmpty()) {
            System.out.println("messageArray is empty");

        } else {
            try {
                oos.writeObject(messageInfo);
                oos.flush();
            } catch (IOException e) {
                System.out.println("Error on sendMessage: " + e);
            }
        }
    }

}// class ServerToServerProcess

class ServerClientProcess extends Thread { // thread for Server to Client communication

    Socket clientSocket;
    ObjectInputStream ois;
    private ArrayList<MessageInfo> messageInfo;

    public ServerClientProcess(Socket clientSocket, ArrayList<MessageInfo> messageInfo) throws IOException {
        System.out.println("Started ServerClientProcess on port " + clientSocket.getLocalPort());
        this.clientSocket = clientSocket;
        this.messageInfo = messageInfo;
        ois = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void run() {
        while (true) {
            String str = receiveMessage();
            System.out.println("Message from client: " + str);
            MessageInfo message = new MessageInfo();
            message.message = str;
            message.timestamp = LocalDateTime.now();
            messageInfo.add(message);
        }
    }

    private String receiveMessage() {
        String str = null;
        try {
            str = (String) ois.readObject();
        } catch (Exception e) {
            System.out.println("Error on command read: " + e);
        }
        return str;
    }

}// class ServerClientProcess
