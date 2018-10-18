package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.Message;

public class ServerConnectionThread extends Thread {

    private Socket socket;
    private ChatServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int id;
    
    public ServerConnectionThread(Socket socket, ChatServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.id = socket.getPort();
        
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
            while (server.getStatus() == 1 && !done) {
    //            if (!server.messages.isEmpty()) {
    //                for (Message m : server.messages {
    //                    out.writeObject(m);
    //                }
    //                server.clients.get(this).clear();
                    try {
                        Message message = (Message) in.readObject();
                        server.process(id, message);
                        done = message.getMessage().equals("!exit");
                    } catch (EOFException e) {
                        done = true;
                    } catch(IOException | ClassNotFoundException e) {
                        done = true;
                        e.printStackTrace();
                    }
                }
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void send(Message msg) throws IOException {
        try {
            out.writeObject(msg);
            out.flush();
        } catch(IOException e) {
            System.out.println(id + " ERROR sending: ");
            e.printStackTrace();
            server.process(id, new Message("xxx", "!exit"));
            close();
        }
    }
    
    public void close() throws IOException {
        System.out.println("Client exited: " + socket);
        in.close();
        socket.close();
    }
}
