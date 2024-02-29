import java.io.*;
import java.net.*;

public class NetworkAdministrator {
    private static int localPort = 8080;
    public static void main(String[] args)  throws Exception{
        try { //Connection building
            System.out.println(localPort);
            try (ServerSocket serverSocket = new ServerSocket(localPort)) {
                System.out.println("Server started. Waiting for clients...");
                while (true) {
                    Socket clientSocket = serverSocket.accept(); //Accept the connection request from client side
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                    // Handle client communication in a separate thread for each client
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    new Thread(clientHandler).start();
                }
            }  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}