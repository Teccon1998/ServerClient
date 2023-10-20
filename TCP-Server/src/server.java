import java.io.*;
import java.net.*;

public class server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6666);
        try {
            
            System.out.println("Server is listening on port "+ serverSocket.getLocalPort() +"...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected, waiting for message from: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + "...");

                try {
                    DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                    String message;
                    while ((message = dataInputStream.readUTF()) != null) {
                        System.out.println("Message received: " + message + "\n");
                        if(message.toLowerCase().substring(0, 3).contains("put"))
                        {
                            System.out.println("Receiving file...\n");
                            int result = receiveFile(dataInputStream,message);
                            if(result == 1)
                            {
                                System.out.println("File successfully received. Sending confirmation message...\n");
                                dataOutputStream.writeUTF("SERVER: File successfully uploaded. Breaking connection...\n");
                                System.out.println("Closing connection...\n");
                                clientSocket.close();
                                if(clientSocket.isClosed())
                                    System.out.println("Connection closed.\n");
                                else
                                    System.out.println("ERROR.");                                                            
                            }
                            else
                            {
                                dataOutputStream.writeUTF("Error receiving file");
                            }
                        }
                        else
                        {
                            dataOutputStream.writeUTF("Message received: " + message);
                        }

                    }
                } catch (IOException e)  {
                    System.out.println("Client disconnected, waiting for another client...");
                    
                } 
            }
        } catch (IOException e) {
            e.printStackTrace();
            serverSocket.close();
        }
    }

    public static int receiveFile(DataInputStream dataInputStream, String message) throws IOException
    {
        try {
            int bytes = 0;
            String[] splitMessage = message.split(" ",2);
            long fileSize = dataInputStream.readLong();
            String fileName = splitMessage[1];
            fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\alexa\\IdeaProjects\\TCPProject\\TCP-Server\\lib\\"+fileName);
            while(fileSize > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1)
            {
                fileOutputStream.write(buffer,0,bytes);
                fileSize -= bytes;
            }
            fileOutputStream.close();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
