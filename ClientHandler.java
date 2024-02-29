import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
public class ClientHandler implements Runnable { //Created to handle the communication for client
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;

        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            clientSocket.setSoTimeout(100000);
            while (true) {
                // Read client messages
                String clientMessage = reader.readLine();
                

                if (clientMessage == null || "exit".equalsIgnoreCase(clientMessage)) {
                    break;
                }
                switch (clientMessage) {
                    case "Help":
                        System.out.println("Recieved Help Message from client "+clientSocket.getInetAddress());
                        HelpFolder();
                        break;
                    case "StartDesktopSharing":
                        startDesktopSharing();
                        break;
                    case "File":
                        receiveFile();
                        break;
                    default:
                        // Handle other messages as needed
                        break;
                }

                // Send a response back to the client
                writer.println("Server received: " + clientMessage);
            }

            // Close resources
            reader.close();
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startDesktopSharing() {
        try{
            sendDesktopScreenshots(clientSocket);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private static void sendDesktopScreenshots(Socket clientSocket) throws Exception {
       ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

            JFrame frame = new JFrame("Desktop Viewer");
            JLabel label = new JLabel();

            frame.getContentPane().add(label);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            while (true) {
                byte[] imageData = (byte[]) inputStream.readObject();
                BufferedImage screenshot = ImageIO.read(new ByteArrayInputStream(imageData));
                label.setIcon(new ImageIcon(screenshot));
                frame.repaint();
            }
        }
    

    private void receiveFile() {
         try {

            InputStream inputStream = clientSocket.getInputStream();
            byte[] fileSizeBytes = new byte[8];
            inputStream.read(fileSizeBytes);
            long fileSize = bytesToLong(fileSizeBytes);
            System.out.println("Receiving file of size " + fileSize + " bytes");

            // Receive file data
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\khand\\Desktop\\CN project\\recieved.txt");
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;
            File directories = new File("C:\\Users\\khand\\Desktop\\CN project");
            if(!directories.exists()) {
                directories.mkdirs();
            } 
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                if (totalBytesRead >= fileSize) {
                    break;
                }
            }

            System.out.println("File received successfully");

            fileOutputStream.close();
            inputStream.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     private static long bytesToLong(byte[] bytes) {
        return ((long) bytes[0] << 56)
                | ((long) bytes[1] & 0xFF) << 48
                | ((long) bytes[2] & 0xFF) << 40
                | ((long) bytes[3] & 0xFF) << 32
                | ((long) bytes[4] & 0xFF) << 24
                | ((long) bytes[5] & 0xFF) << 16
                | ((long) bytes[6] & 0xFF) << 8
                | (bytes[7] & 0xFF);
    }
    private void HelpFolder(){
        File directories = new File("C:\\Users\\khand\\Desktop\\Help Folder");
            if(!directories.exists()) {
                directories.mkdirs();
            } 
    }
}
