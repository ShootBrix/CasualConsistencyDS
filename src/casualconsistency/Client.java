
package casualconsistency;

import java.io.*;
import java.net.*;

public class Client {

    Socket serverSocket;
    Socket clientSupplierSocket;
    //ObjectInputStream ois;
    ObjectOutputStream oos;
    String directoryPath = null;
    int localPeerid;

    public Client() throws IOException {

        System.out.println("Welcome Client: What Server port would you like to connect to?");

        BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in));
        String choice = buffRead.readLine();
        int number = Integer.parseInt(choice);

        serverSocket = new Socket("localhost", number);

        oos = new ObjectOutputStream(serverSocket.getOutputStream());

        System.out.println("Connection has been established with the server on port: " + number);
    }

    public void run() {
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Enter your message: ");
            try {
                String str = buffRead.readLine();
                oos.writeObject(str);
                oos.flush();
            } catch (IOException e) {
                System.out.println("Error on client write message: " + e);
            }
        }
    }
}
