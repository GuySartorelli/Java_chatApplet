package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

import messages.Message;

/**
 * Main application/GUI for number guessing game
 * @author rallion
 */
public class ChatClient implements Runnable {
	
    int status;
	private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public ChatClient(Socket socket, ObjectInputStream in, ObjectOutputStream out) throws IOException {
            this.socket = socket;
            this.out = out;
            this.in = in;
            System.out.println("Client running");
            status = 1;
            new Thread(this).start();
    }
    
    @Override
    public void run() {
        while (status == 1) {
            try {
                Message message = (Message) in.readObject();
                process(message);
            } catch (EOFException e) {
                //Thrown when the server closes connection because it's stuck on in.readObject() when the connection terminates
                System.out.println("Connection severed by server");
                status = 0;
                break;
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
    
    public void send(String msg) {
        if (status == 1) {
            try {
                out.writeObject(new Message(null, msg));
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
            status = 0;
            send("!exit");
            out.close();
            socket.close();
            System.out.println("Client closed");
        }
    }
}