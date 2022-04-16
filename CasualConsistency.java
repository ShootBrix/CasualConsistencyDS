/*
 * Dmitri Gordienko 
 * CMPSC 497 Lab 2
 * Enforcing Casual Consistency in Distributed System 
 */
package casualconsistency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author diman
 */
public class CasualConsistency {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        System.out.println("\nWELCOM USER, do you want to run a Server or a Client?\n");
        System.out.println("Enter your choice:");
        System.out.println("1. Run as Server");
        System.out.println("2. Run as Client");

        BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in));
        String choice = buffRead.readLine();

        if (choice.equals("1")) {
            try {
                Server s = new Server(); // Create a server object
                s.run(); // Start the server
            } catch (IOException e) {
                System.out.println("Server start error " + e.getMessage());
            }
        }
       else if (choice.equals("2")) {
           try {
               Client c = new Client(); // Create a Client object
               c.run(); //Start the Client
           } catch (IOException e) {
               System.out.println("Client start error " + e.getMessage());
           }
       } else {
        System.out.println("Incorrect input. Program Stopped");
       }
    }// main()
}
