package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import messages.Message;

public class ClientConnectionThread extends Thread {
    
    private ChatClient client;
    private ObjectInputStream in;
    
    public ClientConnectionThread(ChatClient client, Socket socket) throws IOException {
        this.client = client;
        this.in = new ObjectInputStream(socket.getInputStream());
    }
    
    public void close() throws IOException {
        in.close();
    }
    
    @Override
    public void run() {
        while (client.status == 1) {
            try {
                Message message = (Message) in.readObject();
                client.process(message);
            } catch (SocketException e) {
                //Thrown when the client ends because it's stuck on in.readObject() when the socket closes
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
