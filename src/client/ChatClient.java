package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import messages.Message;

/**
 * Main application/GUI for number guessing game
 * @author rallion
 */
public class ChatClient {
	
    private int port = 9090;
    int status;
    String serverAddress = "10.140.136.80";
    private ClientConnectionThread reciever;
	private Socket socket;
    private ObjectOutputStream out;
    private String userName = "Guy";
    
    public ChatClient() throws IOException {
        try {
            socket = new Socket(serverAddress, port);
            System.out.println("Client running");
            status = 1;
            
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                socket.close();
            }

            reciever = new ClientConnectionThread(this, socket);
            reciever.start();
            
        } catch (ConnectException e) {
            System.out.println("Unable to connect");
            status = -1;
        }
    }
    
    public void send(String msg) {
        if (status == 1) {
            try {
                out.writeObject(new Message(userName, msg));
                out.flush();
                System.out.println("Me: " + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public synchronized void process(Message message) {
        if (message != null) System.out.println(message);
    }
    
    public void close() throws IOException {
        if (status == 1) {
            send("!exit");
            status = 0;
            out.close();
            reciever.close();
            socket.close();
            System.out.println("Client closed");
        }
    }
}