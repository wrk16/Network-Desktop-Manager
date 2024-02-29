import java.io.*;
import java.net.Socket;

public class ClientSideConnection{ // make the request to server for connection with client and send messages to server
    private static final String SERVER_IP = "192.168.123.132";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server.");

            // Set up input/output streams for communication
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // Open GUI for user interactions
            new ClientGUI(reader, writer,socket);

            // Close resources
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
