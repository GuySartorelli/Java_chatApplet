package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import messages.Message;

public class ClientConnectionThread extends Thread {
    
    private ChatClient client;
    private Socket socket;
    private ObjectInputStream in;
    
    public ClientConnectionThread(ChatClient client, Socket socket) throws IOException {
        this.client = client;
        this.socket = socket;
        this.in = new ObjectInputStream(socket.getInputStream());
    }
    
    public void close() throws IOException {
        in.close();
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                client.process((Message) in.readObject());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
