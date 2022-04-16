
package casualconsistency;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class Client {

    Socket serverSocket;
    Socket clientSupplierSocket;
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
            
            try {
                MessageInfo message = new MessageInfo();
                message.timestamp = LocalDateTime.now();
                
                System.out.println("Enter your message: ");
                message.message = buffRead.readLine();

                System.out.println("What port would you like to Delay? 0 for no delay: ");
                message.delayPortID = Integer.parseInt(buffRead.readLine());
                if(message.delayPortID != 0){
                    System.out.println("How long should it dalay(in sec)? ");
                    message.delayDuration = Integer.parseInt(buffRead.readLine());
                }
                oos.writeObject(message);
                oos.flush();
            } catch (Exception e) {
                System.out.println("Error on client write message: " + e);
            }
        }
    }
}
