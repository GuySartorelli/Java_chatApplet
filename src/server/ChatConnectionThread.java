package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.Message;

public class ChatConnectionThread extends Thread {

    private Socket socket;
    private ChatServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public ChatConnectionThread(Socket socket, ChatServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            socket.close();
        }
        System.out.println("Client accepted: " + socket);
    }
    
    @Override
    public void run() 
    {
        try {
            boolean done = false;
            while (server.getStatus() == 1) {
    //            if (!server.messages.isEmpty()) {
    //                for (Message m : server.messages {
    //                    out.writeObject(m);
    //                }
    //                server.clients.get(this).clear();
                
                    while (!done) {  
                        try {
                            Message message = (Message) in.readObject();
                            System.out.println(message);
                            done = message.getMessage().equals("!exit");
                        } catch (EOFException e) {
                            done = true;
                        } catch(IOException | ClassNotFoundException e) {
                            done = true;
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Client exited: " + socket);
                    in.close();
                    socket.close();
                }
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void close() throws IOException {
        socket.close();
    }
}
