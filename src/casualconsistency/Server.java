
package casualconsistency;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.net.ServerSocket;
import java.util.*;

public class Server {

    private ServerSocket serverMainSocket = null;
    private Socket clientSocket = null;
    private ArrayList<Socket> serverSocketList = new ArrayList<Socket>();
    private ArrayList<Socket> serverAcceptedList = new ArrayList<Socket>();
    private ArrayList<MessageInfo> messageInfo = new ArrayList<MessageInfo>();
    private HashMap<Socket, Status> bChanged = new HashMap<Socket, Status>(); //Couldn't send Booleans over stream without problems.

    public Server() throws IOException {
        System.out.println("Enter a port number for others to connect to: ");

        BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in));
        String choice = buffRead.readLine();
        int number = Integer.parseInt(choice);

        serverMainSocket = new ServerSocket(number);// port for client connections
        System.out.println("Server has started on port: " + serverMainSocket.getLocalPort());
    }

    public void run() throws IOException {

        while (true) {
            Socket serverSocket = connectToOtherServer();
            if (serverSocket != null) {
                serverSocketList.add(serverSocket);
            } else {
                break;
            }
        }

        waitingForClient();

        new ServerToServerConnections(serverMainSocket, messageInfo, bChanged, serverAcceptedList).start();

        for (Socket serverSocket : serverSocketList) {
            new ServerToServerSendProcess(serverSocket, messageInfo, bChanged).start();
        }
        for (Socket serverSocket : serverSocketList) {
            new ServerToServerReceiveProcess(serverSocket, messageInfo).start();
        }

        //while(true);
    }

    private Socket connectToOtherServer() {
        System.out.println("Enter a port number for connection to another existing server OR '0' to continue:");
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in));
        try {
            String choice;
            choice = buffRead.readLine();
            int number = Integer.parseInt(choice);
            if (number != 0) {
                Socket socket = new Socket("localhost", number);
                System.out.println("Connected to port: " + socket.getPort() + " local " + socket.getLocalPort());
                bChanged.put(socket, new Status());
                return socket;
            }
        } catch (IOException e) {
            System.out.println("Error on connection to serverSocket: " + e);
        }
        return null;
    }

    private void waitingForClient() {
        System.out.println("Waiting for a client to connect...");
        try {
            clientSocket = serverMainSocket.accept();
            new ServerClientProcess(clientSocket, messageInfo, bChanged, serverSocketList, serverAcceptedList).start();
        } catch (IOException e) {
            System.out.println("Connection error from Client: " + e);
        } catch (SecurityException e) {
            System.out.println("Security error: " + e);
        }
    }

}// class Server

// *****THREADS*****

class ServerToServerConnections extends Thread { // Thread for new Servers connections

    private ServerSocket serverMainSocket = null;
    private ArrayList<MessageInfo> messageInfo;
    private HashMap<Socket, Status> bChanged;
    private ArrayList<Socket> serverAcceptedList;

    public ServerToServerConnections(ServerSocket serverSocket, ArrayList<MessageInfo> messageInfo,
     HashMap<Socket, Status> bChanged, ArrayList<Socket> serverAcceptedList) {
        System.out.println("Started ServerToServerConnections on local port " + serverSocket.getLocalPort() );
        this.serverMainSocket = serverSocket;
        this.messageInfo = messageInfo;
        this.bChanged = bChanged;
        this.serverAcceptedList = serverAcceptedList;
    }

    public void run() {
        while (true) {
            System.out.println("Waiting for connections from Servers...");
            try {
                Socket newServerSocket = serverMainSocket.accept();
                System.out.println("Connected from port: " + newServerSocket.getPort() + ", local :" + newServerSocket.getLocalPort());
                synchronized (bChanged) {
                    bChanged.put(newServerSocket, new Status());
                }
                serverAcceptedList.add(newServerSocket);
                new ServerToServerSendProcess(newServerSocket, messageInfo, bChanged).start();
                new ServerToServerReceiveProcess(newServerSocket, messageInfo).start();
            } catch (IOException e) {
                System.out.println("Connection error from Server: " + e);
            } catch (SecurityException e) {
                System.out.println("Security error: " + e);
            }
        }
    }

}// class ServerToServerConnections

class ServerClientProcess extends Thread { // thread for Server to Client communication

    private Socket clientSocket;
    private ObjectInputStream ois;
    private ArrayList<MessageInfo> messageInfo;
    private HashMap<Socket, Status> bChanged;
    private ArrayList<Socket> serverSocketList;
    private ArrayList<Socket> serverAcceptedList;

    public ServerClientProcess(Socket clientSocket, ArrayList<MessageInfo> messageInfo, 
                                HashMap<Socket, Status> bChanged, ArrayList<Socket> serverSocketList, 
                                ArrayList<Socket> serverAcceptedList)
            throws IOException {
        System.out.println("Started ServerClientProcess on port " + clientSocket.getPort() + " local " + clientSocket.getLocalPort());
        this.clientSocket = clientSocket;
        this.messageInfo = messageInfo;
        this.bChanged = bChanged;
        this.serverSocketList = serverSocketList;
        this.serverAcceptedList = serverAcceptedList;
        ois = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void run() {
        while (true) {
            String str = receiveMessage();
            System.out.println("Message from client: " + str);
            MessageInfo message = new MessageInfo();
            message.message = str;
            message.timestamp = LocalDateTime.now();
            message.portID = clientSocket.getPort();
            synchronized (bChanged) { // works like mutex to synchronize one object
                messageInfo.add(message);
                for(Socket socket : serverSocketList){
                    Status status = bChanged.get(socket);
                    status.setChanged(true);
                    System.out.println("serverSocketList: status is: " + status  + " on Port " + socket.getPort());
                }
                for(Socket socket : serverAcceptedList){
                    Status status = bChanged.get(socket);
                    status.setChanged(true);
                    System.out.println("serverAcceptedList: status is: " + status  + " on Port " + socket.getPort());
                }
            }
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
