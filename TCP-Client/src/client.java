import java.net.*;

import java.io.*;

public class client {
    
    public static void main(String args[]) throws UnknownHostException, IOException {
        Socket clientSocket;
        
        clientSocket = new Socket("localhost",6666);
        
        DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while(true)
        {
            String msg = bufferedReader.readLine();
            if(msg.toLowerCase().substring(0, 3).contains("put"))
            {
                dataOutputStream.writeUTF(msg);
                dataOutputStream.flush();
                String commandArray[] = msg.split(" ",2);
                String fileName = commandArray[1];
                System.out.println("Sending file.");
                int result = SendFile(fileName, dataOutputStream);
                if(result == 1)
                {
                    System.out.println("File sent successfully.");
                    break;

                }
                else
                {
                    System.out.println("There was an error sending the file, staying connected...");
                }
            }
            dataOutputStream.writeUTF(msg);
            String response = dataInputStream.readUTF();
            System.out.println("RESPONSE: "+response);
        }
    }

    public static int SendFile(String fileName, DataOutputStream dataOutputStream) throws IOException
    {
        int bytes = 0;

        try {
            File file = new File(fileName);
            FileInputStream fileInputStream = new FileInputStream(file);

            dataOutputStream.writeLong(file.length());
            byte[] buffer = new byte[1024];
            while((bytes = fileInputStream.read(buffer))!= -1)
            {
                dataOutputStream.write(buffer,0,bytes);
                dataOutputStream.flush();
            }
            fileInputStream.close();
            return 1;
        } catch (Exception e) {
            System.out.println("There was an error sending the file");
            return -1;
        }
        
    }
}